package com.project.dto.tipoSolicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para estadísticas detalladas de un tipo de solicitud
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoSolicitudEstadisticasResponse {

    private Long tipoSolicitudId;
    private String nombre;

    // Estadísticas de correos
    private Long totalCorreos;
    private Long correosPendientes;
    private Long correosRespondidos;
    private Long correosVencidos;

    // Estadísticas de tiempo
    private Integer plazoDiasPromedio;
    private Integer plazoDiasMinimo;
    private Integer plazoDiasMaximo;
    private Double tiempoPromedioRespuesta; // días reales entre recepción y respuesta

    // Distribución porcentual por estado
    private Double porcentajePendientes;
    private Double porcentajeRespondidos;
    private Double porcentajeVencidos;

    // Cumplimiento de plazos
    private Long correosEnPlazo;
    private Long correosFueraDePlazo;
    private Double porcentajeCumplimiento;
}