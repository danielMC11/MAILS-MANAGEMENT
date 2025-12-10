package com.project.service;

import com.project.dto.entidad.EntidadActualizarRequest;
import com.project.dto.entidad.EntidadCrearRequest;
import com.project.dto.entidad.EntidadResponse;

import java.util.List;

public interface EntidadService {
    EntidadResponse crearEntidad(EntidadCrearRequest entidadCrearRequest);

    EntidadResponse actualizarEntidad(Long id, EntidadActualizarRequest entidadActualizarRequest);

    void eliminarEntidad(Long id);

    List<EntidadResponse> listarEntidades();

    List<EntidadResponse> buscarPorNombre(String nombre);
}