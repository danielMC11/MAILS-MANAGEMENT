package com.project.service;

import com.project.entity.Cuenta;
import com.project.dto.cuenta.CuentaActualizarRequest;
import com.project.dto.cuenta.CuentaCrearRequest;
import com.project.dto.cuenta.CuentaResponse;

public interface CuentaService {
    void guardarCuenta(String correoCompleto);
    CuentaResponse crearCuenta(CuentaCrearRequest cuentaCrearRequest);

    CuentaResponse actualizarCuenta(Long id, CuentaActualizarRequest cuentaActualizarRequest);

    void eliminarCuenta(Long id);
}
