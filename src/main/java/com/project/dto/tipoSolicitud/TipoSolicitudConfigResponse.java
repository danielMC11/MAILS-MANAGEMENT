package com.project.dto.tipoSolicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para la configuración y estadísticas de tipos de solicitud
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoSolicitudConfigResponse {

    // Datos básicos de tipo_solicitud
    private Long id;
    private String nombre;

    // Datos calculados desde la tabla correos
    private Integer plazoDiasPromedio;  // AVG(plazo_respuesta_en_dias)
    private Integer plazoDiasMinimo;    // MIN(plazo_respuesta_en_dias)
    private Integer plazoDiasMaximo;    // MAX(plazo_respuesta_en_dias)

    // Urgencia calculada basada en el plazo promedio
    // ALTA: <= 5 días, MEDIA: 6-15 días, BAJA: > 15 días
    private String urgencia;

    // Descripción generada según el tipo
    private String descripcion;

    // Estado (activo si tiene correos asociados)
    private Boolean activo;

    // Estadísticas de correos asociados
    private Long totalCorreos;
    private Long correosPendientes;
    private Long correosRespondidos;
    private Long correosVencidos;

    // Tiempo promedio real de respuesta (días entre recepción y respuesta)
    private Double tiempoPromedioRespuestaReal;
}