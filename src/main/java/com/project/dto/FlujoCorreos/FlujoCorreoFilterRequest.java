package com.project.dto.flujocorreo;

import com.project.enums.ETAPA;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlujoCorreoFilterRequest {

    // Filtros principales
    private String correoId;
    private Long usuarioId;
    private ETAPA etapa;
    private Long entidadId; // Filtrar por entidad del correo

    // Filtros por estado
    private Boolean enProgreso; // Solo flujos sin fecha_finalizacion
    private Boolean completado; // Solo flujos con fecha_finalizacion
    private Boolean pendienteAsignacion; // Solo flujos sin usuario asignado

    // Filtros por fechas
    private LocalDateTime fechaAsignacionDesde;
    private LocalDateTime fechaAsignacionHasta;
    private LocalDateTime fechaFinalizacionDesde;
    private LocalDateTime fechaFinalizacionHasta;

    // Filtros por etapas específicas
    private Boolean esRecepcion;
    private Boolean esElaboracion;
    private Boolean esRevision;
    private Boolean esAprobacion;
    private Boolean esEnvio;

    // Paginación
    private Integer pagina = 0;
    private Integer tamanoPagina = 20;
    private String ordenarPor = "fechaAsignacion";
    private String direccionOrden = "DESC"; // ASC o DESC
}