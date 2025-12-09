package com.project.dto.correo;

import com.project.enums.ESTADO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CorreoResponse {

    private String id;  // correo_id
    private String idProceso;
    private String asunto;
    private String cuerpoTexto;
    private ESTADO estado;
    private LocalDateTime fechaRecepcion;
    private LocalDateTime fechaRespuesta;
    private Integer plazoRespuestaEnDias;
    private String radicadoEntrada;
    private String radicadoSalida;

    // Información de relaciones
    private Long cuentaId;
    private String nombreCuenta;
    private String correoCuenta;

    private Long tipoSolicitudId;
    private String nombreTipoSolicitud;

    // Información enriquecida de entidad
    private Long entidadId;
    private String nombreEntidad;
    private String dominioEntidad;

    // Campos calculados para estadísticas
    private Boolean vencido;
    private Long diasTranscurridos;
    private Long diasRestantes;
}