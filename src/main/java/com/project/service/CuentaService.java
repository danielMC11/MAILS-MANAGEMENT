package com.project.service;

import com.project.entity.Cuenta;
import com.project.dto.cuenta.CuentaActualizarRequest;
import com.project.dto.cuenta.CuentaCrearRequest;
import com.project.dto.cuenta.CuentaResponse;

import java.util.List;

public interface CuentaService {
    void guardarCuenta(String correoCompleto);

    CuentaResponse crearCuenta(CuentaCrearRequest cuentaCrearRequest);

    CuentaResponse actualizarCuenta(Long id, CuentaActualizarRequest cuentaActualizarRequest);

    void eliminarCuenta(Long id);

    List<CuentaResponse> listarCuentas();

    List<CuentaResponse> buscarCuentas(String query);

    // En CuentaService.java (interface)
    Long contarCuentasPorEntidad(Long entidadId);

}
