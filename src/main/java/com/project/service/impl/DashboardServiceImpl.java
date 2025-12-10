package com.project.service.impl;

import com.project.dto.dashboard.DashboardEstadisticasDTO;
import com.project.dto.dashboard.DashboardEstadisticasResponse;
import com.project.dto.dashboard.MetricaResponse;
import com.project.repository.DashboardRepository;
import com.project.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

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
}