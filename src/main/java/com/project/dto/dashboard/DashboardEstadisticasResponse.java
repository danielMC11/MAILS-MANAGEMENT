package com.project.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class DashboardEstadisticasResponse {

    @JsonProperty("total_correos")
    private Long totalCorreos;

    @JsonProperty("cumplimiento")
    private BigDecimal cumplimiento;

    @JsonProperty("correos_vencidos")
    private Long correosVencidos;

    @JsonProperty("tiempo_promedio")
    private BigDecimal tiempoPromedio;

    @JsonProperty("distribucion_estado")
    private Map<String, Long> distribucionPorEstado;

    @JsonProperty("distribucion_etapa")
    private Map<String, Long> distribucionPorEtapa;

    @JsonProperty("total_entidades")
    private Long totalEntidades;

    @JsonProperty("total_cuentas")
    private Long totalCuentas;

    @JsonProperty("total_usuarios")
    private Long totalUsuarios;
}