package com.project.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardEstadisticasDTO {
    // Métricas principales
    private Long totalCorreos;
    private Long correosRespondidos;
    private Long correosVencidos;
    private Long correosPendientes;
    private BigDecimal cumplimiento; // Porcentaje
    private BigDecimal tiempoPromedioRespuesta; // Días

    // Distribuciones
    private Map<String, Long> distribucionPorEstado;
    private Map<String, Long> distribucionPorEtapa;
    private Map<String, Long> correosPorEntidad;

    // Indicadores
    private Long totalEntidades;
    private Long totalCuentas;
    private Long totalUsuarios;
    private Long solicitudesActivas;
}
