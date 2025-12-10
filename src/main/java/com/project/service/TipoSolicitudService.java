package com.project.service;

import com.project.dto.tipoSolicitud.TipoSolicitudConfigResponse;
import com.project.dto.tipoSolicitud.TipoSolicitudEstadisticasResponse;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de tipos de solicitud
 */
public interface TipoSolicitudService {

    /**
     * Lista todos los tipos de solicitud con sus estadísticas
     * @return Lista de tipos de solicitud con estadísticas calculadas
     */
    List<TipoSolicitudConfigResponse> listarTodos();

    /**
     * Obtiene un tipo de solicitud por su ID con estadísticas
     * @param id ID del tipo de solicitud
     * @return Tipo de solicitud con estadísticas
     */
    TipoSolicitudConfigResponse obtenerPorId(Long id);

    /**
     * Lista solo los tipos de solicitud que tienen correos asociados (activos)
     * @return Lista de tipos de solicitud activos
     */
    List<TipoSolicitudConfigResponse> listarActivos();

    /**
     * Obtiene estadísticas detalladas de un tipo de solicitud
     * @param id ID del tipo de solicitud
     * @return Estadísticas detalladas del tipo de solicitud
     */
    TipoSolicitudEstadisticasResponse obtenerEstadisticas(Long id);
}