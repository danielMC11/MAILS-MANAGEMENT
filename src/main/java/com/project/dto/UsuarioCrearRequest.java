package com.project.dto;

import com.project.validator.ExisteRoles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioCrearRequest {

    @NotBlank(message = "El nombre no puede ser vacío")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 carácteres")
    private String nombres;

    @NotBlank(message = "El apellido no puede ser vacío")
    @Size(min = 3, max = 50, message = "El apellido debe tener entre 3 y 50 carácteres")
    private String apellidos;

    @NotBlank(message = "El número no puede ser vacío")
    @Size(min=10, max=10, message = "El número de teléfono debe tener exactamente 10 dígitos")
    @Pattern(regexp = "^(3[0-2])[0-9]{8}$", message = "Número de teléfono inválido")
    private String numeroCelular;


    @NotBlank(message = "El rol es obligatorio")
    @ExisteRoles
    private String rol;


    @NotBlank(message = "El conjunto de roles no puede estar vacío")
    @Email(message = "Correo electrónico inválido")
    private Set<String> roles;

    @NotBlank(message = "La dirección de correo electrónico es requerida")
    @Email(message = "Correo electrónico inválido")
    String correo;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min=8, max=32, message = "La contraseña debe tener entre 8 y 32 carácteres")
    private String password;

}
