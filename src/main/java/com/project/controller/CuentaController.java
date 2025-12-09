package com.project.controller;

import com.project.dto.cuenta.CuentaActualizarRequest;
import com.project.dto.cuenta.CuentaCrearRequest;
import com.project.dto.cuenta.CuentaResponse;
import com.project.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cuentas/")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping("crear")
    public ResponseEntity<?> crearCuenta(
            @Valid @RequestBody CuentaCrearRequest request) {
        CuentaResponse cuentaCreada = cuentaService.crearCuenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaCreada);
    }

    @PutMapping("actualizar/{id}")
    public ResponseEntity<?> actualizarCuenta(
            @PathVariable("id") Long id,
            @Valid @RequestBody CuentaActualizarRequest request) {
        return ResponseEntity.ok(cuentaService.actualizarCuenta(id, request));
    }

    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<?> eliminarCuenta(@PathVariable("id") Long id) {
        cuentaService.eliminarCuenta(id);
        return ResponseEntity.ok().build();
    }
}