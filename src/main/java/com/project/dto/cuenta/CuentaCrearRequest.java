package com.project.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CuentaCrearRequest {

    @NotBlank(message = "El nombre de la cuenta no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre de la cuenta debe tener entre 3 y 50 caracteres")
    private String nombreCuenta;

    @NotBlank(message = "El correo de la cuenta no puede estar vacío")
    @Size(max = 64, message = "El correo no puede exceder 64 caracteres")
    private String correoCuenta;

    @NotNull(message = "El ID de la entidad es requerido")
    private Long entidadId;
}