package com.project.validator;

import com.project.repository.RolRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

// Archivo: ExisteRolValidator.java
// Archivo: ExisteRolesValidator.java
@Component
public class ExisteRolesValidator implements ConstraintValidator<ExisteRoles, Set<String>> {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public boolean isValid(Set<String> rolesNombres, ConstraintValidatorContext context) {
        if (rolesNombres == null || rolesNombres.isEmpty()) {
            return true; // Ya validado por @NotEmpty, o puedes ajustarlo aquí.
        }

        // Verifica que todos los roles en el Set existan en la DB
        for (String nombre : rolesNombres) {
            if (!rolRepository.findByNombreRol(nombre).isPresent()) {
                // Si encuentras al menos uno que NO existe, la validación falla
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("El rol '" + nombre + "' no existe.")
                        .addConstraintViolation();
                return false;
            }
        }

        return true; // Todos los roles son válidos
    }
}