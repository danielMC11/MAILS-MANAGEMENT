package com.project.dto.correo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CorreoEstadisticasResponse {

    private Long totalCorreos;
    private Long correosPendientes;
    private Long correosRespondidos;
    private Long correosVencidos;

    private Double porcentajeRespondidos;
    private Double porcentajeVencidos;

    private Long tiempoPromedioRespuestaHoras;
    private Long tiempoMaximoRespuestaHoras;
    private Long tiempoMinimoRespuestaHoras;

    // Estadísticas por entidad
    private Map<String, Long> correosPorEntidad; // nombreEntidad -> cantidad

    // Estadísticas por tipo de solicitud
    private Map<String, Long> correosPorTipoSolicitud; // nombreTipo -> cantidad

    // Estadísticas por estado
    private Map<String, Long> correosPorEstado; // estado -> cantidad

    // Estadísticas por mes
    private Map<String, Long> correosPorMes; // "YYYY-MM" -> cantidad

    // Correos próximos a vencer (en los próximos 3 días)
    private Long correosPorVencer;
}