package com.project.controller;


import com.project.dto.entidad.EntidadActualizarRequest;
import com.project.dto.entidad.EntidadCrearRequest;
import com.project.dto.entidad.EntidadResponse;
import com.project.service.EntidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/entidades/")
public class EntidadController {

    private final EntidadService entidadService;

    public EntidadController(EntidadService entidadService) {
        this.entidadService = entidadService;
    }

    @PostMapping("crear")
    public ResponseEntity<?> crearEntidad(
            @Valid @RequestBody EntidadCrearRequest request) {
        EntidadResponse entidadCreada = entidadService.crearEntidad(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(entidadCreada);
    }

    @PutMapping("actualizar/{id}")
    public ResponseEntity<?> actualizarEntidad(
            @PathVariable("id") Long id,
            @Valid @RequestBody EntidadActualizarRequest request) {
        return ResponseEntity.ok(entidadService.actualizarEntidad(id, request));
    }

    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<?> eliminarEntidad(@PathVariable("id") Long id) {
        entidadService.eliminarEntidad(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<EntidadResponse>> listarEntidades() {
        List<EntidadResponse> entidades = entidadService.listarEntidades();
        return ResponseEntity.ok(entidades);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<EntidadResponse>> buscarEntidades(@RequestParam("nombre") String nombre) {
        List<EntidadResponse> entidades = entidadService.buscarPorNombre(nombre);
        return ResponseEntity.ok(entidades);
    }

}