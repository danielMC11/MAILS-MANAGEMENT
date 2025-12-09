package com.project.dto.entidad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EntidadCrearRequest {

    @NotBlank(message = "El nombre de la entidad no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre de la entidad debe tener entre 3 y 50 caracteres")
    private String nombreEntidad;

    @NotBlank(message = "El dominio de correo no puede estar vacío")
    @Size(max = 20, message = "El dominio de correo no puede exceder 20 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9.-]*\\.[a-zA-Z]{2,}$",
            message = "Formato de dominio inválido. Ejemplo: unillanos.edu.co")
    private String dominioCorreo;
}