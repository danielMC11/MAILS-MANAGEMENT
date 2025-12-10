package com.project.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricaResponse {
    private String titulo;
    private String valor;
    private String descripcion;
    private String color;
    private BigDecimal porcentajeCambio;
    private Boolean esPositivo;
}