package com.project.dto.correo;

import com.project.enums.ESTADO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CorreoFilterRequest {

    // Filtros básicos
    private String asunto;
    private ESTADO estado;
    private Long cuentaId;
    private Long entidadId;
    private Long tipoSolicitudId;

    // Filtros por fechas
    private LocalDateTime fechaRecepcionDesde;
    private LocalDateTime fechaRecepcionHasta;
    private LocalDateTime fechaRespuestaDesde;
    private LocalDateTime fechaRespuestaHasta;

    // Filtros por radicados
    private String radicadoEntrada;
    private String radicadoSalida;

    // Filtros por vencimiento
    private Boolean vencido;  // null = todos, true = solo vencidos, false = solo no vencidos
    private Boolean conRespuesta; // null = todos, true = con respuesta, false = sin respuesta

    // Paginación
    private Integer pagina = 0;
    private Integer tamanoPagina = 20;
    private String ordenarPor = "fechaRecepcion";
    private String direccionOrden = "DESC"; // ASC o DESC
}