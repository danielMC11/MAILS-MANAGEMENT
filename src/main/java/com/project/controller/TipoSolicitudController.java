package com.project.controller;

import com.project.dto.tipoSolicitud.TipoSolicitudConfigResponse;
import com.project.dto.tipoSolicitud.TipoSolicitudEstadisticasResponse;
import com.project.service.TipoSolicitudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de tipos de solicitud
 * Proporciona endpoints para consultar tipos de solicitud con sus estadísticas
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tipos-solicitud")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permitir CORS para desarrollo, ajustar en producción
public class TipoSolicitudController {

    private final TipoSolicitudService tipoSolicitudService;

    // ==================== ENDPOINTS DE CONSULTA ====================

    /**
     * GET /api/v1/tipos-solicitud
     * Lista todos los tipos de solicitud con estadísticas calculadas desde la tabla correos
     *
     * @return Lista de tipos de solicitud con estadísticas
     */
    @GetMapping
    public ResponseEntity<List<TipoSolicitudConfigResponse>> listarTodos() {
        log.info("GET /api/v1/tipos-solicitud - Listando todos los tipos de solicitud");
        List<TipoSolicitudConfigResponse> tipos = tipoSolicitudService.listarTodos();
        log.info("Se encontraron {} tipos de solicitud", tipos.size());
        return ResponseEntity.ok(tipos);
    }

    /**
     * GET /api/v1/tipos-solicitud/activos
     * Lista solo los tipos de solicitud que tienen correos asociados (activos)
     *
     * @return Lista de tipos de solicitud activos
     */
    @GetMapping("/activos")
    public ResponseEntity<List<TipoSolicitudConfigResponse>> listarActivos() {
        log.info("GET /api/v1/tipos-solicitud/activos - Listando tipos activos");
        List<TipoSolicitudConfigResponse> tipos = tipoSolicitudService.listarActivos();
        log.info("Se encontraron {} tipos de solicitud activos", tipos.size());
        return ResponseEntity.ok(tipos);
    }

    /**
     * GET /api/v1/tipos-solicitud/{id}
     * Obtiene un tipo de solicitud específico con sus estadísticas
     *
     * @param id ID del tipo de solicitud
     * @return Tipo de solicitud con estadísticas
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipoSolicitudConfigResponse> obtenerPorId(
            @PathVariable("id") Long id) {
        log.info("GET /api/v1/tipos-solicitud/{} - Obteniendo tipo de solicitud", id);
        TipoSolicitudConfigResponse tipo = tipoSolicitudService.obtenerPorId(id);
        return ResponseEntity.ok(tipo);
    }

    /**
     * GET /api/v1/tipos-solicitud/{id}/estadisticas
     * Obtiene estadísticas detalladas de un tipo de solicitud específico
     * Incluye: distribución por estado, cumplimiento de plazos, porcentajes, etc.
     *
     * @param id ID del tipo de solicitud
     * @return Estadísticas detalladas del tipo de solicitud
     */
    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<TipoSolicitudEstadisticasResponse> obtenerEstadisticas(
            @PathVariable("id") Long id) {
        log.info("GET /api/v1/tipos-solicitud/{}/estadisticas - Obteniendo estadísticas", id);
        TipoSolicitudEstadisticasResponse estadisticas = tipoSolicitudService.obtenerEstadisticas(id);
        return ResponseEntity.ok(estadisticas);
    }
}