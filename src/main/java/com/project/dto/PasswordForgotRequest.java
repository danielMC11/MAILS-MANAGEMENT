package com.project.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record PasswordForgotRequest(
		@NotBlank(message = "La dirección de correo electrónico es requerida")
		@Email(message = "Correo electrónico inválido")
		String email
){}
