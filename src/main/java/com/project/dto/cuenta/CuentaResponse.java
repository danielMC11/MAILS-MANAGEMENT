package com.project.dto.cuenta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuentaResponse {

    private Long id;
    private String nombreCuenta;
    private String correoCuenta;
    private Long entidadId;
    private String nombreEntidad; // Opcional: para mostrar info de la entidad
}