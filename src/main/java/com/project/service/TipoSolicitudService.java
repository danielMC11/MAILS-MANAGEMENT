// /service/TipoSolicitudService.java
package com.project.service;

import com.project.dto.tipoSolicitud.TipoSolicitudRequest;
import com.project.dto.tipoSolicitud.TipoSolicitudResponse;

import java.util.List;

public interface TipoSolicitudService {
    // CRUD básico
    List<TipoSolicitudResponse> listarTodos();
    TipoSolicitudResponse buscarPorId(Long id);
    TipoSolicitudResponse crearTipoSolicitud(TipoSolicitudRequest request);
    TipoSolicitudResponse actualizarTipoSolicitud(Long id, TipoSolicitudRequest request);
    void eliminarTipoSolicitud(Long id);

    // Búsqueda
    List<TipoSolicitudResponse> buscarPorNombre(String nombre);
}