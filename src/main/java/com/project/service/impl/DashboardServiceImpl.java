package com.project.service.impl;

import com.project.dto.FiltroCorreoRequestDTO;
import com.project.dto.dashboard.DashboardEstadisticasDTO;
import com.project.dto.dashboard.DashboardEstadisticasResponse;
import com.project.dto.dashboard.MetricaResponse;
import com.project.entity.*;
import com.project.enums.ESTADO;
import com.project.enums.ROL;
import com.project.repository.*;
import com.project.service.DashboardService;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.persistence.criteria.Predicate;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntidadRepository entidadRepository;

    @Autowired
    private TipoSolicitudRepository tipoSolicitudRepository;

    @Autowired
    private CorreoRepository correoRepository;

    @Autowired
    private FlujoCorreoRepository flujoCorreoRepository;

    // Helper method para convertir cualquier objeto a Long de forma segura
    private Long safeToLong(Object obj) {
        if (obj == null) return 0L;

        try {
            if (obj instanceof Number) {
                return ((Number) obj).longValue();
            }
            if (obj instanceof BigInteger) {
                return ((BigInteger) obj).longValue();
            }
            if (obj instanceof BigDecimal) {
                return ((BigDecimal) obj).longValue();
            }
            // PostgreSQL puede devolver Integer para COUNT(*)
            if (obj instanceof Integer) {
                return ((Integer) obj).longValue();
            }
            // Intenta parsear como string
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            return 0L;
        }
    }

    // Helper method para convertir a BigDecimal de forma segura
    private BigDecimal safeToBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;

        try {
            if (obj instanceof BigDecimal) {
                return (BigDecimal) obj;
            }
            if (obj instanceof Number) {
                return BigDecimal.valueOf(((Number) obj).doubleValue());
            }
            if (obj instanceof Double) {
                return BigDecimal.valueOf((Double) obj);
            }
            if (obj instanceof Float) {
                return BigDecimal.valueOf((Float) obj);
            }
            // PostgreSQL puede devolver Double para ROUND()
            if (obj instanceof Double) {
                return BigDecimal.valueOf((Double) obj);
            }
            return new BigDecimal(obj.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public DashboardEstadisticasDTO obtenerEstadisticasCompletas() {
        Map<String, Object> estadisticas = dashboardRepository.obtenerEstadisticasPrincipales();
        List<Object[]> distribucionEstado = dashboardRepository.obtenerDistribucionPorEstado();
        List<Object[]> distribucionEtapa = dashboardRepository.obtenerDistribucionPorEtapa();
        List<Object[]> correosPorEntidad = dashboardRepository.obtenerCorreosPorEntidad();

        // Debug: Imprimir lo que devuelve la consulta
        System.out.println("DEBUG - Estadísticas principales: " + estadisticas);

        // Mapear distribuciones
        Map<String, Long> distribucionPorEstadoMap = new HashMap<>();
        for (Object[] row : distribucionEstado) {
            String estado = (row[0] != null) ? row[0].toString() : "DESCONOCIDO";
            Long cantidad = safeToLong(row[1]);
            distribucionPorEstadoMap.put(estado, cantidad);
        }

        Map<String, Long> distribucionPorEtapaMap = new HashMap<>();
        for (Object[] row : distribucionEtapa) {
            String etapa = (row[0] != null) ? row[0].toString() : "DESCONOCIDO";
            Long cantidad = safeToLong(row[1]);
            distribucionPorEtapaMap.put(etapa, cantidad);
        }

        Map<String, Long> correosPorEntidadMap = new HashMap<>();
        for (Object[] row : correosPorEntidad) {
            String entidad = (row[0] != null) ? row[0].toString() : "DESCONOCIDO";
            Long cantidad = safeToLong(row[1]);
            correosPorEntidadMap.put(entidad, cantidad);
        }

        // Construir el DTO usando métodos seguros
        return DashboardEstadisticasDTO.builder()
                .totalCorreos(safeToLong(estadisticas.get("total_correos")))
                .correosRespondidos(safeToLong(estadisticas.get("correos_respondidos")))
                .correosVencidos(safeToLong(estadisticas.get("correos_vencidos")))
                .correosPendientes(safeToLong(estadisticas.get("correos_pendientes")))
                .cumplimiento(safeToBigDecimal(estadisticas.get("cumplimiento")))
                .tiempoPromedioRespuesta(safeToBigDecimal(estadisticas.get("tiempo_promedio_respuesta")))
                .totalEntidades(safeToLong(estadisticas.get("total_entidades")))
                .totalCuentas(safeToLong(estadisticas.get("total_cuentas")))
                .totalUsuarios(safeToLong(estadisticas.get("total_usuarios")))
                .solicitudesActivas(safeToLong(estadisticas.get("solicitudes_activas")))
                .distribucionPorEstado(distribucionPorEstadoMap)
                .distribucionPorEtapa(distribucionPorEtapaMap)
                .correosPorEntidad(correosPorEntidadMap)
                .build();
    }

    @Override
    public DashboardEstadisticasResponse obtenerEstadisticasDashboard() {
        DashboardEstadisticasDTO dto = obtenerEstadisticasCompletas();

        return DashboardEstadisticasResponse.builder()
                .totalCorreos(dto.getTotalCorreos())
                .cumplimiento(dto.getCumplimiento())
                .correosVencidos(dto.getCorreosVencidos())
                .tiempoPromedio(dto.getTiempoPromedioRespuesta())
                .distribucionPorEstado(dto.getDistribucionPorEstado())
                .distribucionPorEtapa(dto.getDistribucionPorEtapa())
                .totalEntidades(dto.getTotalEntidades())
                .totalCuentas(dto.getTotalCuentas())
                .totalUsuarios(dto.getTotalUsuarios())
                .build();
    }

    @Override
    public List<MetricaResponse> obtenerKPIsPrincipales() {
        Map<String, Object> estadisticas = dashboardRepository.obtenerEstadisticasPrincipales();
        Map<String, Object> estadisticasMes = dashboardRepository.obtenerEstadisticasUltimoMes();

        // Usar métodos seguros para evitar ClassCastException
        Long totalCorreos = safeToLong(estadisticas.get("total_correos"));
        BigDecimal cumplimiento = safeToBigDecimal(estadisticas.get("cumplimiento"));
        BigDecimal tiempoPromedio = safeToBigDecimal(estadisticas.get("tiempo_promedio_respuesta"));
        Long vencidos = safeToLong(estadisticas.get("correos_vencidos"));

        Long totalMes = safeToLong(estadisticasMes.get("total_correos_mes"));
        BigDecimal cumplimientoMes = safeToBigDecimal(estadisticasMes.get("cumplimiento_mes"));

        // Calcular variación solo si hay datos del mes
        BigDecimal variacionCumplimiento = BigDecimal.ZERO;
        Boolean esPositivo = null;

        if (cumplimientoMes.compareTo(BigDecimal.ZERO) > 0 && totalMes > 0) {
            variacionCumplimiento = cumplimiento.subtract(cumplimientoMes)
                    .divide(cumplimientoMes, 2, BigDecimal.ROUND_HALF_UP);
            esPositivo = variacionCumplimiento.compareTo(BigDecimal.ZERO) >= 0;
        }

        return List.of(
                MetricaResponse.builder()
                        .titulo("Total Correos")
                        .valor(totalCorreos.toString())
                        .descripcion("Solicitudes gestionadas")
                        .color("blue")
                        .porcentajeCambio(null)
                        .esPositivo(null)
                        .build(),

                MetricaResponse.builder()
                        .titulo("Cumplimiento")
                        .valor(cumplimiento + "%")
                        .descripcion("Tasa de éxito")
                        .color("green")
                        .porcentajeCambio(variacionCumplimiento)
                        .esPositivo(esPositivo)
                        .build(),

                MetricaResponse.builder()
                        .titulo("Vencidos")
                        .valor(vencidos.toString())
                        .descripcion("Requieren atención")
                        .color("red")
                        .porcentajeCambio(null)
                        .esPositivo(null)
                        .build(),

                MetricaResponse.builder()
                        .titulo("Tiempo Promedio")
                        .valor(tiempoPromedio + "d")
                        .descripcion("Días por solicitud")
                        .color("purple")
                        .porcentajeCambio(null)
                        .esPositivo(null)
                        .build()
        );
    }

    @Override
    public Map<String, Long> obtenerDistribucionPorEstado() {
        List<Object[]> resultados = dashboardRepository.obtenerDistribucionPorEstado();
        return resultados.stream()
                .collect(Collectors.toMap(
                        row -> (row[0] != null) ? row[0].toString() : "DESCONOCIDO",
                        row -> safeToLong(row[1])
                ));
    }

    @Override
    public Map<String, Long> obtenerDistribucionPorEtapa() {
        List<Object[]> resultados = dashboardRepository.obtenerDistribucionPorEtapa();
        return resultados.stream()
                .collect(Collectors.toMap(
                        row -> (row[0] != null) ? row[0].toString() : "DESCONOCIDO",
                        row -> safeToLong(row[1])
                ));
    }

    @Override
    public Map<String, Long> obtenerCorreosPorEntidad() {
        List<Object[]> resultados = dashboardRepository.obtenerCorreosPorEntidad();
        return resultados.stream()
                .collect(Collectors.toMap(
                        row -> (row[0] != null) ? row[0].toString() : "DESCONOCIDO",
                        row -> safeToLong(row[1])
                ));
    }

    @Override
    public List<Map<String, Object>> obtenerGestores() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        List<Map<String, Object>> resultado = usuarios.stream()
                .filter(usuario -> usuario.getRoles().stream().anyMatch(
                        rol -> rol.getNombreRol() == ROL.GESTOR))
                .map(usuario -> {
                    Map<String, Object> gestor = new HashMap<>();
                    gestor.put("id", usuario.getId());
                    gestor.put("nombre", usuario.getNombres() + " " + usuario.getApellidos());
                    return gestor;
                })
                .collect(Collectors.toList());

        // Agregar opción "Todos los gestores"
        Map<String, Object> todos = new HashMap<>();
        todos.put("id", null);
        todos.put("nombre", "Todos los gestores");
        resultado.add(0, todos);

        return resultado;
    }

    @Override
    public List<Map<String, Object>> obtenerEntidades() {
        List<Entidad> entidades = entidadRepository.findAll();

        List<Map<String, Object>> resultado = entidades.stream()
                .map(entidad -> {
                    Map<String, Object> entidadResultado = new HashMap<>();
                    entidadResultado.put("id", entidad.getId());
                    entidadResultado.put("nombre", entidad.getNombreEntidad());
                    return entidadResultado;
                })
                .collect(Collectors.toList());

        Map<String, Object> todos = new HashMap<>();
        todos.put("id", null);
        todos.put("nombre", "Todas las entidades");
        resultado.add(0, todos);

        return resultado;
    }

    @Override
    public List<Map<String, Object>> obtenerTipoSolicitudes() {
        List<TipoSolicitud> tipoSolicitudes = tipoSolicitudRepository.findAll();

        List<Map<String, Object>> resultado = tipoSolicitudes.stream()
                .map(tipoSolicitud -> {
                    Map<String, Object> tipoSolicitudResultado = new HashMap<>();
                    tipoSolicitudResultado.put("id", tipoSolicitud.getId());
                    tipoSolicitudResultado.put("nombre", tipoSolicitud.getNombre());
                    return tipoSolicitudResultado;
                })
                .collect(Collectors.toList());

        Map<String, Object> todos = new HashMap<>();
        todos.put("id", null);
        todos.put("nombre", "Todos los tipos de solicitudes");
        resultado.add(0, todos);

        return resultado;
    }


    @Override
    @Transactional
    public Page<Correo> filtrarCorreos(FiltroCorreoRequestDTO filtro, Pageable pageable) {

        // Construir la especificación (query dinámica) basada en los filtros
        Specification<Correo> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filtrar por fechas (si están presentes)
            if (filtro.getFechaInicio() != null) {
                LocalDateTime inicio = filtro.getFechaInicio().atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("fechaRecepcion"), inicio));
            }

            if (filtro.getFechaFin() != null) {
                LocalDateTime fin = filtro.getFechaFin().atTime(LocalTime.MAX);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("fechaRecepcion"), fin));
            }

            // 2. Filtrar por gestor (usuario asignado en el flujo de correos)
            if (filtro.getGestorId() != null) {
                Subquery<String> subquery = query.subquery(String.class);
                Root<FlujoCorreos> flujoRoot = subquery.from(FlujoCorreos.class);

                // ✅ CORRECTO: Acceder a través de la relación
                subquery.select(flujoRoot.get("correo").get("id"));

                subquery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(flujoRoot.get("usuario").get("id"), filtro.getGestorId()),
                        criteriaBuilder.isNotNull(flujoRoot.get("fechaAsignacion")),
                        criteriaBuilder.isNull(flujoRoot.get("fechaFinalizacion"))
                ));

                // ✅ CORRECTO: String IN Subquery<String>
                predicates.add(criteriaBuilder.in(root.get("id")).value(subquery));
            }

            // 3. Filtrar por entidad (a través de la cuenta)
            if (filtro.getEntidadId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("cuenta").get("entidad").get("id"),
                        filtro.getEntidadId()
                ));
            }

            // 4. Filtrar por estado
            if (filtro.getEstado() != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), filtro.getEstado()));
            }

            // 5. Filtrar por tipo de solicitud
            if (filtro.getTipoSolicitudId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("tipoSolicitud").get("id"),
                        filtro.getTipoSolicitudId()
                ));
            }

            // 6. Filtrar por urgencia
            if (filtro.getUrgencia() != null) {
                predicates.add(criteriaBuilder.equal(root.get("urgencia"), filtro.getUrgencia()));
            }

            // 7. Búsqueda de texto en múltiples campos
            if (filtro.getBuscar() != null && !filtro.getBuscar().trim().isEmpty()) {
                String busqueda = "%" + filtro.getBuscar().toLowerCase() + "%";

                Predicate radicadoEntrada = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("radicadoEntrada")),
                        busqueda
                );

                Predicate radicadoSalida = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("radicadoSalida")),
                        busqueda
                );

                Predicate asunto = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("asunto")),
                        busqueda
                );

                // Buscar en el correo de la cuenta (remitente)
                Predicate remitente = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("cuenta").get("correoCuenta")),
                        busqueda
                );

                Predicate gestionId = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("gestionId")),
                        busqueda
                );

                // Buscar también en el cuerpo del correo si es necesario
                Predicate cuerpoTexto = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("cuerpoTexto")),
                        busqueda
                );

                predicates.add(criteriaBuilder.or(
                        radicadoEntrada, radicadoSalida, asunto, remitente, gestionId, cuerpoTexto
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Aplicar la especificación y la paginación
        return correoRepository.findAll(spec, pageable);
    }

}