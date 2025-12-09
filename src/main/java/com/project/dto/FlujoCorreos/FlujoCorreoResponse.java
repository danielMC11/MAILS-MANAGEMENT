package com.project.dto.flujocorreo;

import com.project.enums.ETAPA;
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
public class FlujoCorreoResponse {

    private Long id;
    private String correoId;
    private String asuntoCorreo; // Para mostrar en UI
    private Long usuarioId;
    private String nombreUsuario; // Para mostrar en UI
    private ETAPA etapa;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaFinalizacion;

    // Información adicional para UI
    private Long duracionHoras; // Si está finalizado
    private Boolean enProgreso; // Si tiene fecha asignación pero no finalización
    private String estadoEtapa; // "PENDIENTE", "EN_PROGRESO", "COMPLETADO"
}