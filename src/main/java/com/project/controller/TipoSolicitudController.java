// /controller/TipoSolicitudController.java
package com.project.controller;

import com.project.dto.tipoSolicitud.TipoSolicitudRequest;
import com.project.dto.tipoSolicitud.TipoSolicitudResponse;
import com.project.service.TipoSolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión CRUD de tipos de solicitud
 * Solo maneja la entidad básica: id y nombre
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tipos-solicitud")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TipoSolicitudController {

    private final TipoSolicitudService tipoSolicitudService;

    // ==================== ENDPOINTS CRUD ====================

    /**
     * GET /api/v1/tipos-solicitud
     * Obtiene todos los tipos de solicitud
     */
    @GetMapping
    public ResponseEntity<List<TipoSolicitudResponse>> listarTodos() {
        log.info("GET /api/v1/tipos-solicitud - Listando todos los tipos");
        List<TipoSolicitudResponse> tipos = tipoSolicitudService.listarTodos();
        return ResponseEntity.ok(tipos);
    }

    /**
     * GET /api/v1/tipos-solicitud/{id}
     * Obtiene un tipo de solicitud por su ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipoSolicitudResponse> obtenerPorId(@PathVariable("id") Long id) {
        log.info("GET /api/v1/tipos-solicitud/{} - Obteniendo tipo", id);
        TipoSolicitudResponse tipo = tipoSolicitudService.buscarPorId(id);
        return ResponseEntity.ok(tipo);
    }

    /**
     * GET /api/v1/tipos-solicitud/buscar
     * Busca tipos de solicitud por nombre
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<TipoSolicitudResponse>> buscarPorNombre(
            @RequestParam("nombre") String nombre) {
        log.info("GET /api/v1/tipos-solicitud/buscar?nombre={} - Buscando por nombre", nombre);
        List<TipoSolicitudResponse> tipos = tipoSolicitudService.buscarPorNombre(nombre);
        return ResponseEntity.ok(tipos);
    }

    /**
     * POST /api/v1/tipos-solicitud
     * Crea un nuevo tipo de solicitud
     */
    @PostMapping
    public ResponseEntity<TipoSolicitudResponse> crearTipoSolicitud(
            @Valid @RequestBody TipoSolicitudRequest request) {
        log.info("POST /api/v1/tipos-solicitud - Creando tipo: {}", request.getNombre());
        TipoSolicitudResponse nuevoTipo = tipoSolicitudService.crearTipoSolicitud(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTipo);
    }

    /**
     * PUT /api/v1/tipos-solicitud/{id}
     * Actualiza un tipo de solicitud existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<TipoSolicitudResponse> actualizarTipoSolicitud(
            @PathVariable("id") Long id,
            @Valid @RequestBody TipoSolicitudRequest request) {
        log.info("PUT /api/v1/tipos-solicitud/{} - Actualizando a: {}", id, request.getNombre());
        TipoSolicitudResponse tipoActualizado = tipoSolicitudService.actualizarTipoSolicitud(id, request);
        return ResponseEntity.ok(tipoActualizado);
    }

    /**
     * DELETE /api/v1/tipos-solicitud/{id}
     * Elimina un tipo de solicitud
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTipoSolicitud(@PathVariable("id") Long id) {
        log.info("DELETE /api/v1/tipos-solicitud/{} - Eliminando tipo", id);
        tipoSolicitudService.eliminarTipoSolicitud(id);
        return ResponseEntity.noContent().build();
    }
}