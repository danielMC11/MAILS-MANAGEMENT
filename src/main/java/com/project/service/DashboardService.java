package com.project.service;

import com.project.dto.FiltroCorreoRequestDTO;
import com.project.dto.dashboard.DashboardEstadisticasDTO;
import com.project.dto.dashboard.DashboardEstadisticasResponse;
import com.project.dto.dashboard.MetricaResponse;
import com.project.entity.Correo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    DashboardEstadisticasDTO obtenerEstadisticasCompletas();

    DashboardEstadisticasResponse obtenerEstadisticasDashboard();

    List<MetricaResponse> obtenerKPIsPrincipales();

    Map<String, Long> obtenerDistribucionPorEstado();

    Map<String, Long> obtenerDistribucionPorEtapa();

    Map<String, Long> obtenerCorreosPorEntidad();

    List<Map<String, Object>> obtenerGestores();

    List<Map<String, Object>> obtenerEntidades();

    List<Map<String, Object>> obtenerTipoSolicitudes();

    Page<Correo> filtrarCorreos(FiltroCorreoRequestDTO filtro, Pageable pageable);


}