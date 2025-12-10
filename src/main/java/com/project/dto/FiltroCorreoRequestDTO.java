package com.project.dto;


import com.project.enums.ESTADO;
import com.project.enums.URGENCIA;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class FiltroCorreoRequestDTO {

    // Filtros de fecha
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaInicio;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaFin;

    // Filtros de selección
    private Long gestorId;          // ID del usuario gestor
    private Long entidadId;         // ID de la entidad
    private ESTADO estado;          // Estado del correo
    private Long tipoSolicitudId;   // ID del tipo de solicitud
    private URGENCIA urgencia;      // Nivel de urgencia

    // Búsqueda de texto
    private String buscar;          // Texto para buscar en múltiples campos

    // Método para verificar si hay filtros activos
    public boolean tieneFiltrosActivos() {
        return fechaInicio != null ||
                fechaFin != null ||
                gestorId != null ||
                entidadId != null ||
                estado != null ||
                tipoSolicitudId != null ||
                urgencia != null ||
                (buscar != null && !buscar.trim().isEmpty());
    }

    // Método para obtener una descripción de los filtros aplicados
    public String getDescripcionFiltros() {
        List<String> filtros = new ArrayList<>();

        if (fechaInicio != null && fechaFin != null) {
            filtros.add("Período: " + fechaInicio + " a " + fechaFin);
        }

        if (gestorId != null) {
            filtros.add("Gestor específico");
        }

        if (entidadId != null) {
            filtros.add("Entidad específica");
        }

        if (estado != null) {
            filtros.add("Estado: " + estado);
        }

        if (tipoSolicitudId != null) {
            filtros.add("Tipo de solicitud específico");
        }

        if (urgencia != null) {
            filtros.add("Urgencia: " + urgencia);
        }

        if (buscar != null && !buscar.trim().isEmpty()) {
            filtros.add("Búsqueda: \"" + buscar + "\"");
        }

        return filtros.isEmpty() ? "Sin filtros" : String.join(" | ", filtros);
    }
}
