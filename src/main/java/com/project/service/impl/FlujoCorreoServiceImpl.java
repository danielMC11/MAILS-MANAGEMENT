package com.project.service.impl;

import com.project.entity.Correo;
import com.project.entity.FlujoCorreos;
import com.project.entity.Usuario;
import com.project.enums.ETAPA;
import com.project.repository.CorreoRepository;
import com.project.repository.FlujoCorreoRepository;
import com.project.repository.UsuarioRepository;
import com.project.service.FlujoCorreoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import com.project.dto.flujocorreo.FlujoCorreoEstadisticasResponse;
import com.project.dto.flujocorreo.FlujoCorreoFilterRequest;
import com.project.dto.flujocorreo.FlujoCorreoResponse;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.Duration;
import java.util.*;
        import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlujoCorreoServiceImpl implements FlujoCorreoService {



    private final FlujoCorreoRepository flujoCorreoRepository;
    private final CorreoRepository correoRepository;
    private final UsuarioRepository usuarioRepository;


    @Override
    public FlujoCorreos iniciarFlujo(String correoId, String correoResponsable, ETAPA etapa, LocalDateTime fechaAsignacion) {

        Correo correo = correoRepository.findById(correoId)
                .orElseThrow(() -> new RuntimeException("ID correo no encontrado"));

        Usuario usuario = usuarioRepository.findByCorreo(correoResponsable)
                .orElseThrow(() -> new RuntimeException("Correo no encontrado"));


        FlujoCorreos flujoCorreo = new FlujoCorreos();
        flujoCorreo.setCorreo(correo);
        flujoCorreo.setUsuario(usuario);
        flujoCorreo.setEtapa(etapa);
        flujoCorreo.setFechaAsignacion(fechaAsignacion);
        return flujoCorreoRepository.save(flujoCorreo);

    }

    @Override
    public FlujoCorreos terminarFlujo(Long flujoId, LocalDateTime fechaFinalizacion) {

        FlujoCorreos flujoCorreo = flujoCorreoRepository.findById(flujoId)
                .orElseThrow(() -> new RuntimeException("ID flujo no encontrado"));

        flujoCorreo.setFechaFinalizacion(fechaFinalizacion);

        return flujoCorreoRepository.save(flujoCorreo);

    }

    @Override
    public FlujoCorreoResponse obtenerFlujoCorreo(Long id) {
        FlujoCorreos flujo = flujoCorreoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Flujo de correo no encontrado con ID: " + id
                ));
        return construirResponse(flujo);
    }

    @Override
    public Page<FlujoCorreoResponse> buscarFlujosCorreo(FlujoCorreoFilterRequest filtro) {
        Pageable pageable = construirPageable(filtro);
        Specification<FlujoCorreos> spec = construirSpecification(filtro);
        Page<FlujoCorreos> flujosPage = flujoCorreoRepository.findAll(spec, pageable);
        return flujosPage.map(this::construirResponse);
    }

    @Override
    public List<FlujoCorreoResponse> obtenerFlujosPorCorreo(String correoId) {
        List<FlujoCorreos> flujos = flujoCorreoRepository.findByCorreoIdOrderByFechaAsignacionDesc(correoId);
        return flujos.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlujoCorreoResponse> obtenerHistorialCompletoCorreo(String correoId) {
        // Obtener todos los flujos del correo ordenados cronológicamente
        List<FlujoCorreos> flujos = flujoCorreoRepository.findByCorreoIdOrderByFechaAsignacionAsc(correoId);
        return flujos.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlujoCorreoResponse> obtenerFlujosPorUsuario(Long usuarioId) {
        List<FlujoCorreos> flujos = flujoCorreoRepository.findByUsuarioId(usuarioId);
        return flujos.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlujoCorreoResponse> obtenerFlujosPendientesUsuario(Long usuarioId) {
        List<FlujoCorreos> flujos = flujoCorreoRepository.findByUsuarioIdAndFechaFinalizacionIsNull(usuarioId);
        return flujos.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlujoCorreoResponse> obtenerFlujosEnProgresoUsuario(Long usuarioId) {
        List<FlujoCorreos> flujos = flujoCorreoRepository.findByUsuarioIdAndFechaFinalizacionIsNull(usuarioId);
        return flujos.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlujoCorreoResponse> obtenerFlujosPorEtapa(String etapa) {
        ETAPA etapaEnum = ETAPA.valueOf(etapa.toUpperCase());
        List<FlujoCorreos> flujos = flujoCorreoRepository.findByEtapa(etapaEnum);
        return flujos.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlujoCorreoResponse> obtenerFlujosEnProgresoPorEtapa(String etapa) {
        ETAPA etapaEnum = ETAPA.valueOf(etapa.toUpperCase());
        List<FlujoCorreos> flujos = flujoCorreoRepository.findByEtapaAndFechaFinalizacionIsNull(etapaEnum);
        return flujos.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlujoCorreoResponse> obtenerFlujosEnProgreso() {
        List<FlujoCorreos> flujos = flujoCorreoRepository.findByFechaFinalizacionIsNull();
        return flujos.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlujoCorreoResponse> obtenerFlujosCompletados() {
        List<FlujoCorreos> flujos = flujoCorreoRepository.findByFechaFinalizacionIsNotNull();
        return flujos.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlujoCorreoResponse> obtenerFlujosSinAsignar() {
        List<FlujoCorreos> flujos = flujoCorreoRepository.findByUsuarioIsNull();
        return flujos.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlujoCorreoResponse> obtenerUltimosFlujosCompletados(Integer limite) {
        List<FlujoCorreos> flujos = flujoCorreoRepository.findTopNByFechaFinalizacionIsNotNullOrderByFechaFinalizacionDesc(limite);
        return flujos.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlujoCorreoResponse> obtenerFlujosConMayorTiempo(Integer limite) {
        List<FlujoCorreos> flujos = flujoCorreoRepository.findTopNByFechaFinalizacionIsNotNullOrderByDuracionDesc(limite);
        return flujos.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FlujoCorreoEstadisticasResponse obtenerEstadisticas() {
        Long totalFlujos = flujoCorreoRepository.count();
        Long flujosEnProgreso = flujoCorreoRepository.countByFechaFinalizacionIsNull();
        Long flujosCompletados = flujoCorreoRepository.countByFechaFinalizacionIsNotNull();

        // Estadísticas por etapa
        Map<ETAPA, Long> flujosPorEtapa = new EnumMap<>(ETAPA.class);
        for (ETAPA etapa : ETAPA.values()) {
            Long count = flujoCorreoRepository.countByEtapa(etapa);
            flujosPorEtapa.put(etapa, count);
        }

        // Estadísticas por usuario
        List<Object[]> usuariosStats = flujoCorreoRepository.countFlujosPorUsuario();
        Map<String, Long> flujosPorUsuario = new HashMap<>();
        for (Object[] stat : usuariosStats) {
            String nombre = stat[0] != null ? stat[0].toString() : "Sin asignar";
            Long count = stat[1] != null ? ((Number) stat[1]).longValue() : 0L;
            flujosPorUsuario.put(nombre, count);
        }

        // Estadísticas por entidad
        List<Object[]> entidadesStats = flujoCorreoRepository.countFlujosPorEntidad();
        Map<String, Long> flujosPorEntidad = new HashMap<>();
        for (Object[] stat : entidadesStats) {
            String entidad = stat[0] != null ? stat[0].toString() : "Sin entidad";
            Long count = stat[1] != null ? ((Number) stat[1]).longValue() : 0L;
            flujosPorEntidad.put(entidad, count);
        }

        return FlujoCorreoEstadisticasResponse.builder()
                .totalFlujos(totalFlujos)
                .flujosEnProgreso(flujosEnProgreso)
                .flujosCompletados(flujosCompletados)
                .flujosPorEtapa(flujosPorEtapa)
                .flujosPorUsuario(flujosPorUsuario)
                .flujosPorEntidad(flujosPorEntidad)
                .build();
    }

    @Override
    public FlujoCorreoEstadisticasResponse obtenerEstadisticasPorPeriodo(
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // Implementación similar con filtro de fechas
        return obtenerEstadisticas(); // Placeholder
    }

    @Transactional
    @Override
    public FlujoCorreoResponse asignarUsuario(Long flujoId, Long usuarioId) {
        FlujoCorreos flujo = flujoCorreoRepository.findById(flujoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Flujo de correo no encontrado con ID: " + flujoId
                ));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado con ID: " + usuarioId
                ));

        flujo.setUsuario(usuario);
        flujo.setFechaAsignacion(LocalDateTime.now());

        FlujoCorreos flujoActualizado = flujoCorreoRepository.save(flujo);
        return construirResponse(flujoActualizado);
    }

    @Transactional
    @Override
    public FlujoCorreoResponse finalizarEtapa(Long flujoId) {
        FlujoCorreos flujo = flujoCorreoRepository.findById(flujoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Flujo de correo no encontrado con ID: " + flujoId
                ));

        if (flujo.getFechaFinalizacion() != null) {
            throw new IllegalStateException("Esta etapa ya fue finalizada anteriormente");
        }

        flujo.setFechaFinalizacion(LocalDateTime.now());

        FlujoCorreos flujoActualizado = flujoCorreoRepository.save(flujo);
        return construirResponse(flujoActualizado);
    }

    @Transactional
    @Override
    public FlujoCorreoResponse reasignarFlujo(Long flujoId, Long nuevoUsuarioId) {
        FlujoCorreos flujo = flujoCorreoRepository.findById(flujoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Flujo de correo no encontrado con ID: " + flujoId
                ));

        Usuario nuevoUsuario = usuarioRepository.findById(nuevoUsuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado con ID: " + nuevoUsuarioId
                ));

        flujo.setUsuario(nuevoUsuario);
        flujo.setFechaAsignacion(LocalDateTime.now());

        FlujoCorreos flujoActualizado = flujoCorreoRepository.save(flujo);
        return construirResponse(flujoActualizado);
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private Pageable construirPageable(FlujoCorreoFilterRequest filtro) {
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

    private Specification<FlujoCorreos> construirSpecification(FlujoCorreoFilterRequest filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro.getCorreoId() != null) {
                predicates.add(cb.equal(root.get("correo").get("id"), filtro.getCorreoId()));
            }

            if (filtro.getUsuarioId() != null) {
                predicates.add(cb.equal(root.get("usuario").get("id"), filtro.getUsuarioId()));
            }

            if (filtro.getEtapa() != null) {
                predicates.add(cb.equal(root.get("etapa"), filtro.getEtapa()));
            }

            if (filtro.getEntidadId() != null) {
                predicates.add(cb.equal(
                        root.get("correo").get("cuenta").get("entidad").get("id"),
                        filtro.getEntidadId()
                ));
            }

            if (filtro.getEnProgreso() != null && filtro.getEnProgreso()) {
                predicates.add(cb.isNull(root.get("fechaFinalizacion")));
            }

            if (filtro.getCompletado() != null && filtro.getCompletado()) {
                predicates.add(cb.isNotNull(root.get("fechaFinalizacion")));
            }

            if (filtro.getPendienteAsignacion() != null && filtro.getPendienteAsignacion()) {
                predicates.add(cb.isNull(root.get("usuario")));
            }

            if (filtro.getFechaAsignacionDesde() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("fechaAsignacion"),
                        filtro.getFechaAsignacionDesde()
                ));
            }

            if (filtro.getFechaAsignacionHasta() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("fechaAsignacion"),
                        filtro.getFechaAsignacionHasta()
                ));
            }

            if (filtro.getFechaFinalizacionDesde() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("fechaFinalizacion"),
                        filtro.getFechaFinalizacionDesde()
                ));
            }

            if (filtro.getFechaFinalizacionHasta() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("fechaFinalizacion"),
                        filtro.getFechaFinalizacionHasta()
                ));
            }

            // Filtros por etapas específicas
            if (filtro.getEsRecepcion() != null && filtro.getEsRecepcion()) {
                predicates.add(cb.equal(root.get("etapa"), ETAPA.RECEPCION));
            }

            if (filtro.getEsElaboracion() != null && filtro.getEsElaboracion()) {
                predicates.add(cb.equal(root.get("etapa"), ETAPA.ELABORACION));
            }

            if (filtro.getEsRevision() != null && filtro.getEsRevision()) {
                predicates.add(cb.equal(root.get("etapa"), ETAPA.REVISION));
            }

            if (filtro.getEsAprobacion() != null && filtro.getEsAprobacion()) {
                predicates.add(cb.equal(root.get("etapa"), ETAPA.APROBACION));
            }

            if (filtro.getEsEnvio() != null && filtro.getEsEnvio()) {
                predicates.add(cb.equal(root.get("etapa"), ETAPA.ENVIO));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private FlujoCorreoResponse construirResponse(FlujoCorreos flujo) {
        Long duracionHoras = null;
        Boolean enProgreso = flujo.getFechaFinalizacion() == null;
        String estadoEtapa = determinarEstadoEtapa(flujo);

        if (flujo.getFechaAsignacion() != null && flujo.getFechaFinalizacion() != null) {
            Duration duracion = Duration.between(
                    flujo.getFechaAsignacion(),
                    flujo.getFechaFinalizacion()
            );
            duracionHoras = duracion.toHours();
        }

        return FlujoCorreoResponse.builder()
                .id(flujo.getId())
                .correoId(flujo.getCorreo() != null ? flujo.getCorreo().getId() : null)
                .asuntoCorreo(flujo.getCorreo() != null ? flujo.getCorreo().getAsunto() : null)
                .usuarioId(flujo.getUsuario() != null ? flujo.getUsuario().getId() : null)
                .nombreUsuario(flujo.getUsuario() != null ?
                        flujo.getUsuario().getNombres() + " " + flujo.getUsuario().getApellidos() : null)
                .etapa(flujo.getEtapa())
                .fechaAsignacion(flujo.getFechaAsignacion())
                .fechaFinalizacion(flujo.getFechaFinalizacion())
                .duracionHoras(duracionHoras)
                .enProgreso(enProgreso)
                .estadoEtapa(estadoEtapa)
                .build();
    }

    private String determinarEstadoEtapa(FlujoCorreos flujo) {
        if (flujo.getFechaFinalizacion() != null) {
            return "COMPLETADO";
        } else if (flujo.getFechaAsignacion() != null) {
            return "EN_PROGRESO";
        } else {
            return "PENDIENTE";
        }
    }
}