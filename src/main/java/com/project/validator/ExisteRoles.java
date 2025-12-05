package com.project.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Archivo: ExisteRoles.java
// Archivo: ExisteRoles.java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExisteRolesValidator.class) // Apunta al nuevo validador
public @interface ExisteRoles {
    String message() default "Uno o más roles proporcionados no son válidos";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}