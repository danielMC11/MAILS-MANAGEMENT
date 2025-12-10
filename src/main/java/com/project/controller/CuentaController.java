package com.project.controller;

import com.project.dto.cuenta.CuentaActualizarRequest;
import com.project.dto.cuenta.CuentaCrearRequest;
import com.project.dto.cuenta.CuentaResponse;
import com.project.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cuentas")  // CORREGIDO: Sin barra al final
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping("/crear")  // Agregué barra al inicio
    public ResponseEntity<?> crearCuenta(
            @Valid @RequestBody CuentaCrearRequest request) {
        CuentaResponse cuentaCreada = cuentaService.crearCuenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaCreada);
    }

    @PutMapping("/actualizar/{id}")  // Agregué barra al inicio
    public ResponseEntity<?> actualizarCuenta(
            @PathVariable("id") Long id,
            @Valid @RequestBody CuentaActualizarRequest request) {
        return ResponseEntity.ok(cuentaService.actualizarCuenta(id, request));
    }

    @DeleteMapping("/eliminar/{id}")  // Agregué barra al inicio
    public ResponseEntity<?> eliminarCuenta(@PathVariable("id") Long id) {
        cuentaService.eliminarCuenta(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> listarCuentas() {
        return ResponseEntity.ok(cuentaService.listarCuentas());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<CuentaResponse>> buscarCuentas(@RequestParam("query") String query) {
        return ResponseEntity.ok(cuentaService.buscarCuentas(query));
    }

    @GetMapping("/contar/{entidadId}")
    public ResponseEntity<Long> contarCuentasPorEntidad(@PathVariable("entidadId") Long entidadId) {
        return ResponseEntity.ok(cuentaService.contarCuentasPorEntidad(entidadId));
    }
}