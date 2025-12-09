package com.project.service;

import com.project.entity.FlujoCorreos;
import com.project.enums.ETAPA;

import com.project.dto.flujocorreo.FlujoCorreoEstadisticasResponse;
import com.project.dto.flujocorreo.FlujoCorreoFilterRequest;
import com.project.dto.flujocorreo.FlujoCorreoResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;


public interface FlujoCorreoService {

    FlujoCorreos iniciarFlujo(String correoId, String correoResponsable, ETAPA etapa, LocalDateTime fechaAsignacion);

    FlujoCorreos terminarFlujo(Long flujoId, LocalDateTime fechaFinalizacion);

    // Consultas básicas
    FlujoCorreoResponse obtenerFlujoCorreo(Long id);
    Page<FlujoCorreoResponse> buscarFlujosCorreo(FlujoCorreoFilterRequest filtro);

    // Consultas por correo
    List<FlujoCorreoResponse> obtenerFlujosPorCorreo(String correoId);
    List<FlujoCorreoResponse> obtenerHistorialCompletoCorreo(String correoId);

    // Consultas por usuario
    List<FlujoCorreoResponse> obtenerFlujosPorUsuario(Long usuarioId);
    List<FlujoCorreoResponse> obtenerFlujosPendientesUsuario(Long usuarioId);
    List<FlujoCorreoResponse> obtenerFlujosEnProgresoUsuario(Long usuarioId);

    // Consultas por etapa
    List<FlujoCorreoResponse> obtenerFlujosPorEtapa(String etapa);
    List<FlujoCorreoResponse> obtenerFlujosEnProgresoPorEtapa(String etapa);

    // Consultas especiales
    List<FlujoCorreoResponse> obtenerFlujosEnProgreso();
    List<FlujoCorreoResponse> obtenerFlujosCompletados();
    List<FlujoCorreoResponse> obtenerFlujosSinAsignar();

    // Consultas para dashboard
    List<FlujoCorreoResponse> obtenerUltimosFlujosCompletados(Integer limite);
    List<FlujoCorreoResponse> obtenerFlujosConMayorTiempo(Integer limite);

    // Estadísticas
    FlujoCorreoEstadisticasResponse obtenerEstadisticas();
    FlujoCorreoEstadisticasResponse obtenerEstadisticasPorPeriodo(
            LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Métodos para actualización (si se permite desde API)
    FlujoCorreoResponse asignarUsuario(Long flujoId, Long usuarioId);
    FlujoCorreoResponse finalizarEtapa(Long flujoId);
    FlujoCorreoResponse reasignarFlujo(Long flujoId, Long nuevoUsuarioId);


}
