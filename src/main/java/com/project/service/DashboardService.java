package com.project.service;

import com.project.dto.dashboard.DashboardEstadisticasDTO;
import com.project.dto.dashboard.DashboardEstadisticasResponse;
import com.project.dto.dashboard.MetricaResponse;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    DashboardEstadisticasDTO obtenerEstadisticasCompletas();

    DashboardEstadisticasResponse obtenerEstadisticasDashboard();

    List<MetricaResponse> obtenerKPIsPrincipales();

    Map<String, Long> obtenerDistribucionPorEstado();

    Map<String, Long> obtenerDistribucionPorEtapa();

    Map<String, Long> obtenerCorreosPorEntidad();
}