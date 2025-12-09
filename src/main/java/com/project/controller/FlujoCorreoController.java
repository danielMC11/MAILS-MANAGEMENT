package com.project.controller;

import com.project.dto.flujocorreo.FlujoCorreoEstadisticasResponse;
import com.project.dto.flujocorreo.FlujoCorreoFilterRequest;
import com.project.dto.flujocorreo.FlujoCorreoResponse;
import com.project.service.FlujoCorreoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/flujos-correo")
@RequiredArgsConstructor
public class FlujoCorreoController {

    private final FlujoCorreoService flujoCorreoService;

    // ==================== BÚSQUEDA Y FILTRADO ====================

    @PostMapping("/search")
    public ResponseEntity<Page<FlujoCorreoResponse>> buscarFlujosCorreo(
            @RequestBody FlujoCorreoFilterRequest filtro) {
        Page<FlujoCorreoResponse> flujos = flujoCorreoService.buscarFlujosCorreo(filtro);
        return ResponseEntity.ok(flujos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlujoCorreoResponse> obtenerFlujoCorreo(@PathVariable("id") Long id) {
        return ResponseEntity.ok(flujoCorreoService.obtenerFlujoCorreo(id));
    }

    // ==================== CONSULTAS POR CORREO ====================

    @GetMapping("/correo/{correoId}")
    public ResponseEntity<List<FlujoCorreoResponse>> obtenerFlujosPorCorreo(
            @PathVariable("correoId") String correoId) {
        return ResponseEntity.ok(flujoCorreoService.obtenerFlujosPorCorreo(correoId));
    }

    @GetMapping("/correo/{correoId}/historial")
    public ResponseEntity<List<FlujoCorreoResponse>> obtenerHistorialCompletoCorreo(
            @PathVariable("correoId") String correoId) {
        return ResponseEntity.ok(flujoCorreoService.obtenerHistorialCompletoCorreo(correoId));
    }

    // ==================== CONSULTAS POR USUARIO ====================

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<FlujoCorreoResponse>> obtenerFlujosPorUsuario(
            @PathVariable("usuarioId") Long usuarioId) {
        return ResponseEntity.ok(flujoCorreoService.obtenerFlujosPorUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/pendientes")
    public ResponseEntity<List<FlujoCorreoResponse>> obtenerFlujosPendientesUsuario(
            @PathVariable("usuarioId") Long usuarioId) {
        return ResponseEntity.ok(flujoCorreoService.obtenerFlujosPendientesUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/en-progreso")
    public ResponseEntity<List<FlujoCorreoResponse>> obtenerFlujosEnProgresoUsuario(
            @PathVariable("usuarioId") Long usuarioId) {
        return ResponseEntity.ok(flujoCorreoService.obtenerFlujosEnProgresoUsuario(usuarioId));
    }

    // ==================== CONSULTAS POR ETAPA ====================

    @GetMapping("/etapa/{etapa}")
    public ResponseEntity<List<FlujoCorreoResponse>> obtenerFlujosPorEtapa(
            @PathVariable("etapa") String etapa) {
        return ResponseEntity.ok(flujoCorreoService.obtenerFlujosPorEtapa(etapa));
    }

    @GetMapping("/etapa/{etapa}/en-progreso")
    public ResponseEntity<List<FlujoCorreoResponse>> obtenerFlujosEnProgresoPorEtapa(
            @PathVariable("etapa") String etapa) {
        return ResponseEntity.ok(flujoCorreoService.obtenerFlujosEnProgresoPorEtapa(etapa));
    }

    // ==================== CONSULTAS ESPECIALES ====================

    @GetMapping("/en-progreso")
    public ResponseEntity<List<FlujoCorreoResponse>> obtenerFlujosEnProgreso() {
        return ResponseEntity.ok(flujoCorreoService.obtenerFlujosEnProgreso());
    }

    @GetMapping("/completados")
    public ResponseEntity<List<FlujoCorreoResponse>> obtenerFlujosCompletados() {
        return ResponseEntity.ok(flujoCorreoService.obtenerFlujosCompletados());
    }

    @GetMapping("/sin-asignar")
    public ResponseEntity<List<FlujoCorreoResponse>> obtenerFlujosSinAsignar() {
        return ResponseEntity.ok(flujoCorreoService.obtenerFlujosSinAsignar());
    }

    @GetMapping("/ultimos-completados")
    public ResponseEntity<List<FlujoCorreoResponse>> obtenerUltimosFlujosCompletados(
            @RequestParam(defaultValue = "10") Integer limite) {
        return ResponseEntity.ok(flujoCorreoService.obtenerUltimosFlujosCompletados(limite));
    }

    @GetMapping("/mayor-tiempo")
    public ResponseEntity<List<FlujoCorreoResponse>> obtenerFlujosConMayorTiempo(
            @RequestParam(defaultValue = "10") Integer limite) {
        return ResponseEntity.ok(flujoCorreoService.obtenerFlujosConMayorTiempo(limite));
    }

    // ==================== ESTADÍSTICAS ====================

    @GetMapping("/estadisticas")
    public ResponseEntity<FlujoCorreoEstadisticasResponse> obtenerEstadisticas() {
        return ResponseEntity.ok(flujoCorreoService.obtenerEstadisticas());
    }

    @GetMapping("/estadisticas/periodo")
    public ResponseEntity<FlujoCorreoEstadisticasResponse> obtenerEstadisticasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(flujoCorreoService.obtenerEstadisticasPorPeriodo(inicio, fin));
    }

    // ==================== OPERACIONES ====================

    @PutMapping("/{id}/asignar-usuario")
    public ResponseEntity<FlujoCorreoResponse> asignarUsuario(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId) {
        return ResponseEntity.ok(flujoCorreoService.asignarUsuario(id, usuarioId));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<FlujoCorreoResponse> finalizarEtapa(@PathVariable("id") Long id) {
        return ResponseEntity.ok(flujoCorreoService.finalizarEtapa(id));
    }

    @PutMapping("/{id}/reasignar")
    public ResponseEntity<FlujoCorreoResponse> reasignarFlujo(
            @PathVariable("id") Long id,
            @RequestParam("nuevoUsuarioId") Long nuevoUsuarioId) {
        return ResponseEntity.ok(flujoCorreoService.reasignarFlujo(id, nuevoUsuarioId));
    }
}