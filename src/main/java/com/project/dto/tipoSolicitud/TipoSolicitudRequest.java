// /dto/tipoSolicitud/TipoSolicitudRequest.java
package com.project.dto.tipoSolicitud;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoSolicitudRequest {

    @NotBlank(message = "El nombre del tipo de solicitud es requerido")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;
}