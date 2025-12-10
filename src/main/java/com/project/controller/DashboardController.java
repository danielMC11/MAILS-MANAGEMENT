package com.project.controller;

import com.project.dto.dashboard.DashboardEstadisticasDTO;
import com.project.dto.dashboard.DashboardEstadisticasResponse;
import com.project.dto.dashboard.MetricaResponse;
import com.project.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/estadisticas")
    public ResponseEntity<DashboardEstadisticasDTO> obtenerEstadisticasCompletas() {
        return ResponseEntity.ok(dashboardService.obtenerEstadisticasCompletas());
    }

    @GetMapping("/estadisticas/correos")
    public ResponseEntity<DashboardEstadisticasResponse> obtenerEstadisticasCorreos() {
        return ResponseEntity.ok(dashboardService.obtenerEstadisticasDashboard());
    }

    @GetMapping("/distribucion/estado")
    public ResponseEntity<Map<String, Long>> obtenerDistribucionPorEstado() {
        return ResponseEntity.ok(dashboardService.obtenerDistribucionPorEstado());
    }

    @GetMapping("/distribucion/etapa")
    public ResponseEntity<Map<String, Long>> obtenerDistribucionPorEtapa() {
        return ResponseEntity.ok(dashboardService.obtenerDistribucionPorEtapa());
    }

    @GetMapping("/indicadores")
    public ResponseEntity<List<MetricaResponse>> obtenerKPIsPrincipales() {
        return ResponseEntity.ok(dashboardService.obtenerKPIsPrincipales());
    }

}