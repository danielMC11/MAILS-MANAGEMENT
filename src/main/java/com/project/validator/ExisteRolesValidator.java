package com.project.validator;

import com.project.enums.ROL;
import com.project.repository.RolRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

// Archivo: ExisteRolValidator.java
// Archivo: ExisteRolesValidator.java
@Component
public class ExisteRolesValidator implements ConstraintValidator<ExisteRoles, Set<ROL>> {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public boolean isValid(Set<ROL> rolesNombres, ConstraintValidatorContext context) {
        if (rolesNombres == null || rolesNombres.isEmpty()) {
            return true; // Ya validado por @NotEmpty, o puedes ajustarlo aquí.
        }

        // Verifica que todos los roles en el Set existan en la DB
        for (ROL nombre : rolesNombres) {
            if (rolRepository.findByNombreRol(nombre).isEmpty()) {
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