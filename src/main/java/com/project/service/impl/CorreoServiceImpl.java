package com.project.service.impl;

import com.project.dto.correo.CorreoEstadisticasResponse;
import com.project.dto.correo.CorreoFilterRequest;
import com.project.dto.correo.CorreoResponse;
import com.project.entity.Correo;
import com.project.entity.TipoSolicitud;
import com.project.enums.ESTADO;
import com.project.enums.URGENCIA;
import com.project.repository.CorreoRepository;
import com.project.repository.TipoSolicitudRepository;
import com.project.service.CorreoService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CorreoServiceImpl implements CorreoService {

    private final CorreoRepository correoRepository;
    private final TipoSolicitudRepository tipoSolicitudRepository;

    @Override
    public Correo registrarNuevoCorreo(Correo correo) {
        validarCorreo(correo);
        return correoRepository.save(correo);
    }

    @Override
    public void registrarEnvioFinal(String correoId, LocalDateTime fechaRespuesta) {
        Correo correo = obtenerCorreoEntity(correoId);
        correo.setEstado(ESTADO.RESPONDIDO);
        correo.setFechaRespuesta(fechaRespuesta);
        correoRepository.save(correo);
    }


    @Override
    public void ingresarDatosEntrada(String correoId, String radicadoEntrada, Integer plazoRespuestaEnDias, String tipoSolicitudNombre, String nivelUrgencia) {
        Correo correo = obtenerCorreoEntity(correoId);

        if (radicadoEntrada != null && !radicadoEntrada.equals("")) {
            correo.setRadicadoEntrada(null);
        } else {
            correo.setRadicadoEntrada(radicadoEntrada);
        }

        correo.setPlazoRespuestaEnDias(plazoRespuestaEnDias);

        if(tipoSolicitudNombre != null && !tipoSolicitudNombre.isEmpty()) {
            tipoSolicitudRepository.findByNombre(tipoSolicitudNombre)
                    .ifPresent(correo::setTipoSolicitud);
        }

        if (nivelUrgencia != null) {
            URGENCIA urgenciaEnum = URGENCIA.valueOf(nivelUrgencia.toUpperCase());
            correo.setUrgencia(urgenciaEnum);
        }

        correoRepository.save(correo);
    }

    @Override
    public void ingresarRadicadoSalida(String correoId, String radicadoSalida) {
        Correo correo = obtenerCorreoEntity(correoId);

        correo.setRadicadoSalida(radicadoSalida);

        correoRepository.save(correo);
    }


    @Override
    public void ingresarGestionId(String correoId, String gestionId) {
        Correo correo = obtenerCorreoEntity(correoId);

        correo.setGestionId(gestionId);

        correoRepository.save(correo);
    }


    @Override
    public void vencerCorreo(String correoId) {
        Correo correo = obtenerCorreoEntity(correoId);

        correo.setEstado(ESTADO.VENCIDO);

        correoRepository.save(correo);
    }

    @Override
    public CorreoResponse obtenerCorreoPorId(String id) {
        Correo correo = obtenerCorreoEntity(id);
        return construirResponse(correo);
    }

    @Override
    public CorreoResponse obtenerCorreoPorRadicadoEntrada(String radicado) {
        Correo correo = correoRepository.findByRadicadoEntrada(radicado)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Correo no encontrado con radicado de entrada: " + radicado
                ));
        return construirResponse(correo);
    }

    @Override
    public CorreoResponse obtenerCorreoPorRadicadoSalida(String radicado) {
        Correo correo = correoRepository.findByRadicadoSalida(radicado)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Correo no encontrado con radicado de salida: " + radicado
                ));
        return construirResponse(correo);
    }

    @Override
    public Page<CorreoResponse> listarCorreos(CorreoFilterRequest filtro) {
        Pageable pageable = construirPageable(filtro);
        Specification<Correo> spec = construirSpecification(filtro);
        Page<Correo> correosPage = correoRepository.findAll(spec, pageable);
        return correosPage.map(this::construirResponse);
    }

    @Override
    public List<CorreoResponse> buscarCorreosPorAsunto(String asunto) {
        return correoRepository.findByAsuntoContainingIgnoreCase(asunto)
                .stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CorreoResponse> obtenerCorreosPorCuenta(Long cuentaId) {
        return convertirListaAResponse(correoRepository.findByCuentaId(cuentaId));
    }

    @Override
    public List<CorreoResponse> obtenerCorreosPorEntidad(Long entidadId) {
        return convertirListaAResponse(correoRepository.findByCuentaEntidadId(entidadId));
    }

    @Override
    public List<CorreoResponse> obtenerCorreosPorTipoSolicitud(Long tipoSolicitudId) {
        return convertirListaAResponse(correoRepository.findByTipoSolicitudId(tipoSolicitudId));
    }

    @Override
    public List<CorreoResponse> obtenerCorreosPorEstado(String estado) {
        ESTADO estadoEnum = ESTADO.valueOf(estado.toUpperCase());
        return convertirListaAResponse(correoRepository.findByEstado(estadoEnum));
    }

    @Override
    public List<CorreoResponse> obtenerCorreosVencidos() {
        return convertirListaAResponse(correoRepository.findCorreosVencidos());
    }

    @Override
    public List<CorreoResponse> obtenerCorreosPorVencer(Integer dias) {
        LocalDateTime fechaLimite = LocalDateTime.now().plusDays(dias);
        return convertirListaAResponse(correoRepository.findCorreosPorVencer(fechaLimite));
    }

    @Override
    public List<CorreoResponse> obtenerCorreosSinRespuesta() {
        return convertirListaAResponse(correoRepository.findByEstado(ESTADO.PENDIENTE));
    }

    @Override
    public List<CorreoResponse> obtenerCorreosConRespuestaReciente(Integer dias) {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(dias);
        return convertirListaAResponse(
                correoRepository.findCorreosRespondidosRecientemente(fechaLimite)
        );
    }

    @Override
    public CorreoEstadisticasResponse obtenerEstadisticas() {
        // Obtener conteos básicos
        Long total = correoRepository.countTotalCorreos();
        Long pendientes = correoRepository.countCorreosPendientes();
        Long respondidos = correoRepository.countCorreosRespondidos();
        Long vencidos = correoRepository.countCorreosVencidos();

        // Calcular porcentajes
        Double porcentajeRespondidos = total > 0 ? (respondidos * 100.0 / total) : 0.0;
        Double porcentajeVencidos = total > 0 ? (vencidos * 100.0 / total) : 0.0;

        // Obtener correos por vencer (próximos 3 días)
        LocalDateTime fechaLimite = LocalDateTime.now().plusDays(3);
        Long correosPorVencer = (long) correoRepository.findCorreosPorVencer(fechaLimite).size();

        // Convertir listas de Object[] a Maps
        Map<String, Long> correosPorEntidad = convertirListaAMap(
                correoRepository.countCorreosPorEntidad()
        );

        Map<String, Long> correosPorTipoSolicitud = convertirListaAMap(
                correoRepository.countCorreosPorTipoSolicitud()
        );

        Map<String, Long> correosPorMes = convertirListaAMap(
                correoRepository.countCorreosPorMes()
        );

        // Crear estadísticas por estado
        Map<String, Long> correosPorEstado = new HashMap<>();
        correosPorEstado.put("PENDIENTE", pendientes);
        correosPorEstado.put("RESPONDIDO", respondidos);
        correosPorEstado.put("VENCIDO", vencidos);

        return CorreoEstadisticasResponse.builder()
                .totalCorreos(total)
                .correosPendientes(pendientes)
                .correosRespondidos(respondidos)
                .correosVencidos(vencidos)
                .porcentajeRespondidos(porcentajeRespondidos)
                .porcentajeVencidos(porcentajeVencidos)
                .correosPorVencer(correosPorVencer)
                .correosPorEntidad(correosPorEntidad)
                .correosPorTipoSolicitud(correosPorTipoSolicitud)
                .correosPorEstado(correosPorEstado)
                .correosPorMes(correosPorMes)
                .tiempoPromedioRespuestaHoras(null) // TODO: Implementar si necesario
                .tiempoMaximoRespuestaHoras(null)
                .tiempoMinimoRespuestaHoras(null)
                .build();
    }

    @Override
    public CorreoEstadisticasResponse obtenerEstadisticasPorPeriodo(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {
        // Similar a obtenerEstadisticas pero con filtro de fechas
        // Por ahora retorna estadísticas generales
        return obtenerEstadisticas();
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private Correo obtenerCorreoEntity(String correoId) {
        return correoRepository.findById(correoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Correo no encontrado con ID: " + correoId
                ));
    }

    private void validarCorreo(Correo correo) {
        if (correo.getAsunto() == null || correo.getAsunto().trim().isEmpty()) {
            throw new IllegalArgumentException("El asunto del correo es obligatorio");
        }
        if (correo.getCuenta() == null) {
            throw new IllegalArgumentException("La cuenta asociada es obligatoria");
        }
        if (correo.getFechaRecepcion() == null) {
            throw new IllegalArgumentException("La fecha de recepción es obligatoria");
        }
    }

    private List<CorreoResponse> convertirListaAResponse(List<Correo> correos) {
        return correos.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    private Map<String, Long> convertirListaAMap(List<Object[]> lista) {
        Map<String, Long> mapa = new HashMap<>();
        for (Object[] obj : lista) {
            String key = obj[0] != null ? obj[0].toString() : "Sin categoría";
            Long value = obj[1] != null ? ((Number) obj[1]).longValue() : 0L;
            mapa.put(key, value);
        }
        return mapa;
    }

    private Pageable construirPageable(CorreoFilterRequest filtro) {
        Sort sort = Sort.by(
                filtro.getDireccionOrden().equalsIgnoreCase("ASC")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC,
                filtro.getOrdenarPor()
        );

        return PageRequest.of(
                filtro.getPagina(),
                filtro.getTamanoPagina(),
                sort
        );
    }

    private Specification<Correo> construirSpecification(CorreoFilterRequest filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro.getAsunto() != null && !filtro.getAsunto().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("asunto")),
                        "%" + filtro.getAsunto().toLowerCase() + "%"
                ));
            }

            if (filtro.getEstado() != null) {
                predicates.add(cb.equal(root.get("estado"), filtro.getEstado()));
            }

            if (filtro.getCuentaId() != null) {
                predicates.add(cb.equal(root.get("cuenta").get("id"), filtro.getCuentaId()));
            }

            if (filtro.getEntidadId() != null) {
                predicates.add(cb.equal(
                        root.get("cuenta").get("entidad").get("id"),
                        filtro.getEntidadId()
                ));
            }

            if (filtro.getTipoSolicitudId() != null) {
                predicates.add(cb.equal(
                        root.get("tipoSolicitud").get("id"),
                        filtro.getTipoSolicitudId()
                ));
            }

            if (filtro.getFechaRecepcionDesde() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("fechaRecepcion"),
                        filtro.getFechaRecepcionDesde()
                ));
            }

            if (filtro.getFechaRecepcionHasta() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("fechaRecepcion"),
                        filtro.getFechaRecepcionHasta()
                ));
            }

            if (filtro.getFechaRespuestaDesde() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("fechaRespuesta"),
                        filtro.getFechaRespuestaDesde()
                ));
            }

            if (filtro.getFechaRespuestaHasta() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("fechaRespuesta"),
                        filtro.getFechaRespuestaHasta()
                ));
            }

            if (filtro.getRadicadoEntrada() != null) {
                predicates.add(cb.equal(root.get("radicadoEntrada"), filtro.getRadicadoEntrada()));
            }

            if (filtro.getRadicadoSalida() != null) {
                predicates.add(cb.equal(root.get("radicadoSalida"), filtro.getRadicadoSalida()));
            }

            if (filtro.getConRespuesta() != null) {
                if (filtro.getConRespuesta()) {
                    predicates.add(cb.isNotNull(root.get("fechaRespuesta")));
                } else {
                    predicates.add(cb.isNull(root.get("fechaRespuesta")));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    private CorreoResponse construirResponse(Correo correo) {
        DatosVencimiento vencimiento = calcularDatosVencimiento(correo);

        return CorreoResponse.builder()
                .id(correo.getId())
                .idProceso(correo.getIdProceso())
                .asunto(correo.getAsunto())
                .cuerpoTexto(correo.getCuerpoTexto())
                .estado(correo.getEstado())
                .fechaRecepcion(correo.getFechaRecepcion())
                .fechaRespuesta(correo.getFechaRespuesta())
                .plazoRespuestaEnDias(correo.getPlazoRespuestaEnDias())
                .radicadoEntrada(correo.getRadicadoEntrada())
                .radicadoSalida(correo.getRadicadoSalida())
                .cuentaId(correo.getCuenta() != null ? correo.getCuenta().getId() : null)
                .nombreCuenta(correo.getCuenta() != null ? correo.getCuenta().getNombreCuenta() : null)
                .correoCuenta(correo.getCuenta() != null ? correo.getCuenta().getCorreoCuenta() : null)
                .tipoSolicitudId(correo.getTipoSolicitud() != null ? correo.getTipoSolicitud().getId() : null)
                .nombreTipoSolicitud(correo.getTipoSolicitud() != null ? correo.getTipoSolicitud().getNombreTipoSolicitud() : null)
                .entidadId(correo.getCuenta() != null && correo.getCuenta().getEntidad() != null
                        ? correo.getCuenta().getEntidad().getId() : null)
                .nombreEntidad(correo.getCuenta() != null && correo.getCuenta().getEntidad() != null
                        ? correo.getCuenta().getEntidad().getNombreEntidad() : null)
                .dominioEntidad(correo.getCuenta() != null && correo.getCuenta().getEntidad() != null
                        ? correo.getCuenta().getEntidad().getDominioCorreo() : null)
                .estado(correo.getEstado())
                .vencido(vencimiento.vencido)
                .diasTranscurridos(vencimiento.diasTranscurridos)
                .diasRestantes(vencimiento.diasRestantes)
                .build();
    }


    private DatosVencimiento calcularDatosVencimiento(Correo correo) {
        if (correo.getEstado() != ESTADO.PENDIENTE ||
                correo.getFechaRecepcion() == null ||
                correo.getPlazoRespuestaEnDias() == null) {
            return new DatosVencimiento(false, null, null);
        }

        LocalDateTime fechaVencimiento = correo.getFechaRecepcion()
                .plusDays(correo.getPlazoRespuestaEnDias());

        long diasTranscurridos = ChronoUnit.DAYS.between(
                correo.getFechaRecepcion(),
                LocalDateTime.now()
        );

        long diasRestantes = ChronoUnit.DAYS.between(
                LocalDateTime.now(),
                fechaVencimiento
        );

        boolean vencido = LocalDateTime.now().isAfter(fechaVencimiento);

        return new DatosVencimiento(vencido, diasTranscurridos, diasRestantes);
    }


    private static class DatosVencimiento {
        final boolean vencido;
        final Long diasTranscurridos;
        final Long diasRestantes;

        DatosVencimiento(boolean vencido, Long diasTranscurridos, Long diasRestantes) {
            this.vencido = vencido;
            this.diasTranscurridos = diasTranscurridos;
            this.diasRestantes = diasRestantes;
        }
    }

    // Clase interna simple (no record)
    
}

