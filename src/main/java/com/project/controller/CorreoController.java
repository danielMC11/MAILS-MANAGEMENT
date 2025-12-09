package com.project.controller;

import com.project.dto.correo.CorreoEstadisticasResponse;
import com.project.dto.correo.CorreoFilterRequest;
import com.project.dto.correo.CorreoResponse;
import com.project.service.CorreoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/correos")
@RequiredArgsConstructor
public class CorreoController {

    private final CorreoService correoService;

    // ==================== BÚSQUEDA Y FILTRADO (PRIMERO PARA EVITAR CONFLICTOS) ====================

    /**
     * POST /api/v1/correos/search
     */
    @PostMapping("/search")
    public ResponseEntity<Page<CorreoResponse>> buscarCorreos(
            @RequestBody CorreoFilterRequest filtro) {
        Page<CorreoResponse> correos = correoService.listarCorreos(filtro);
        return ResponseEntity.ok(correos);
    }

    /**
     * GET /api/v1/correos/asunto?texto=...
     */
    @GetMapping("/asunto")
    public ResponseEntity<List<CorreoResponse>> buscarPorAsunto(
            @RequestParam("texto") String texto) {
        List<CorreoResponse> correos = correoService.buscarCorreosPorAsunto(texto);
        return ResponseEntity.ok(correos);
    }

    // ==================== CONSULTAS POR RELACIONES ====================

    /**
     * GET /api/v1/correos/cuenta/{cuentaId}
     */
    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<CorreoResponse>> obtenerPorCuenta(
            @PathVariable("cuentaId") Long cuentaId) {
        List<CorreoResponse> correos = correoService.obtenerCorreosPorCuenta(cuentaId);
        return ResponseEntity.ok(correos);
    }

    /**
     * GET /api/v1/correos/entidad/{entidadId}
     */
    @GetMapping("/entidad/{entidadId}")
    public ResponseEntity<List<CorreoResponse>> obtenerPorEntidad(
            @PathVariable("entidadId") Long entidadId) {
        List<CorreoResponse> correos = correoService.obtenerCorreosPorEntidad(entidadId);
        return ResponseEntity.ok(correos);
    }

    /**
     * GET /api/v1/correos/tipo-solicitud/{tipoSolicitudId}
     */
    @GetMapping("/tipo-solicitud/{tipoSolicitudId}")
    public ResponseEntity<List<CorreoResponse>> obtenerPorTipoSolicitud(
            @PathVariable("tipoSolicitudId") Long tipoSolicitudId) {
        List<CorreoResponse> correos = correoService.obtenerCorreosPorTipoSolicitud(tipoSolicitudId);
        return ResponseEntity.ok(correos);
    }

    /**
     * GET /api/v1/correos/estado/{estado}
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CorreoResponse>> obtenerPorEstado(
            @PathVariable("estado") String estado) {
        List<CorreoResponse> correos = correoService.obtenerCorreosPorEstado(estado);
        return ResponseEntity.ok(correos);
    }

    // ==================== CONSULTAS ESPECIALES ====================

    /**
     * GET /api/v1/correos/vencidos
     */
    @GetMapping("/vencidos")
    public ResponseEntity<List<CorreoResponse>> obtenerVencidos() {
        List<CorreoResponse> correos = correoService.obtenerCorreosVencidos();
        return ResponseEntity.ok(correos);
    }

    /**
     * GET /api/v1/correos/por-vencer?dias=5
     */
    @GetMapping("/por-vencer")
    public ResponseEntity<List<CorreoResponse>> obtenerPorVencer(
            @RequestParam("dias") Integer dias) {
        List<CorreoResponse> correos = correoService.obtenerCorreosPorVencer(dias);
        return ResponseEntity.ok(correos);
    }

    /**
     * GET /api/v1/correos/sin-respuesta
     */
    @GetMapping("/sin-respuesta")
    public ResponseEntity<List<CorreoResponse>> obtenerSinRespuesta() {
        List<CorreoResponse> correos = correoService.obtenerCorreosSinRespuesta();
        return ResponseEntity.ok(correos);
    }

    /**
     * GET /api/v1/correos/respuestas-recientes?dias=7
     */
    @GetMapping("/respuestas-recientes")
    public ResponseEntity<List<CorreoResponse>> obtenerRespuestasRecientes(
            @RequestParam("dias") Integer dias) {
        List<CorreoResponse> correos = correoService.obtenerCorreosConRespuestaReciente(dias);
        return ResponseEntity.ok(correos);
    }

    // ==================== CONSULTAS POR RADICADO ====================

    /**
     * GET /api/v1/correos/radicado-entrada/{radicado}
     */
    @GetMapping("/radicado-entrada/{radicado}")
    public ResponseEntity<CorreoResponse> obtenerPorRadicadoEntrada(
            @PathVariable("radicado") String radicado) {
        CorreoResponse correo = correoService.obtenerCorreoPorRadicadoEntrada(radicado);
        return ResponseEntity.ok(correo);
    }

    /**
     * GET /api/v1/correos/radicado-salida/{radicado}
     */
    @GetMapping("/radicado-salida/{radicado}")
    public ResponseEntity<CorreoResponse> obtenerPorRadicadoSalida(
            @PathVariable("radicado") String radicado) {
        CorreoResponse correo = correoService.obtenerCorreoPorRadicadoSalida(radicado);
        return ResponseEntity.ok(correo);
    }

    // ==================== ESTADÍSTICAS ====================

    /**
     * GET /api/v1/correos/estadisticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<CorreoEstadisticasResponse> obtenerEstadisticas() {
        CorreoEstadisticasResponse estadisticas = correoService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }

    // ==================== CONSULTAS INDIVIDUALES (AL FINAL PARA EVITAR CONFLICTOS) ====================

    /**
     * GET /api/v1/correos/{id}
     * IMPORTANTE: Este endpoint va al final porque /{id} captura cualquier cosa
     */
    @GetMapping("/{id}")
    public ResponseEntity<CorreoResponse> obtenerCorreoPorId(
            @PathVariable("id") String id) {
        CorreoResponse correo = correoService.obtenerCorreoPorId(id);
        return ResponseEntity.ok(correo);
    }
}