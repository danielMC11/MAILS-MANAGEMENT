package com.project.service.impl;

import com.project.dto.entidad.EntidadActualizarRequest;
import com.project.dto.entidad.EntidadCrearRequest;
import com.project.dto.entidad.EntidadResponse;
import com.project.entity.Entidad;
import com.project.repository.CuentaRepository;
import com.project.repository.EntidadRepository;
import com.project.service.EntidadService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntidadServiceImpl implements EntidadService {

    @Autowired
    private EntidadRepository entidadRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Override
    public EntidadResponse crearEntidad(EntidadCrearRequest request) {

        // Validar que no exista una entidad con el mismo dominio (aunque BD lo permita)
        if (entidadRepository.existsByDominioCorreo(request.getDominioCorreo())) {
            throw new IllegalArgumentException(
                    "Ya existe una entidad con el dominio: " + request.getDominioCorreo()
            );
        }

        // Crear la Entidad
        Entidad nuevaEntidad = new Entidad();
        nuevaEntidad.setNombreEntidad(request.getNombreEntidad());
        nuevaEntidad.setDominioCorreo(request.getDominioCorreo());

        // Guardar la Entidad
        Entidad entidadGuardada = entidadRepository.save(nuevaEntidad);

        // Construir y retornar Response
        return toEntidadResponse(entidadGuardada);
    }

    @Transactional
    @Override
    public EntidadResponse actualizarEntidad(Long id, EntidadActualizarRequest request) {

        // Buscar la entidad existente
        Entidad entidad = entidadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entidad no encontrada con ID: " + id));

        // Si cambia el dominio, validar que no exista otro con ese dominio
        if (!entidad.getDominioCorreo().equals(request.getDominioCorreo())) {
            if (entidadRepository.existsByDominioCorreo(request.getDominioCorreo())) {
                throw new IllegalArgumentException(
                        "Ya existe una entidad con el dominio: " + request.getDominioCorreo()
                );
            }
        }

        // Actualizar campos
        entidad.setNombreEntidad(request.getNombreEntidad());
        entidad.setDominioCorreo(request.getDominioCorreo());

        // Guardar cambios
        Entidad entidadActualizada = entidadRepository.save(entidad);

        // Construir y retornar Response
        return toEntidadResponse(entidadActualizada);
    }

    @Override
    public void eliminarEntidad(Long id) {

        // Buscar la entidad
        Entidad entidad = entidadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entidad no encontrada con ID: " + id));

        // Validar que no tenga cuentas asociadas
        if (cuentaRepository.existsByEntidadId(id)) {
            Long count = cuentaRepository.countByEntidadId(id);
            throw new IllegalStateException(
                    "No se puede eliminar la entidad porque tiene " + count + " cuenta(s) asociada(s)"
            );
        }

        // Eliminar la entidad
        entidadRepository.delete(entidad);
    }

    @Override
    public List<EntidadResponse> listarEntidades() {
        return entidadRepository.findAll().stream()
                .map(this::toEntidadResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EntidadResponse> buscarPorNombre(String nombre) {
        return entidadRepository.findByNombreEntidadContainingIgnoreCase(nombre).stream()
                .map(this::toEntidadResponse)
                .collect(Collectors.toList());
    }

    private EntidadResponse toEntidadResponse(Entidad entidad) {
        return EntidadResponse.builder()
                .id(entidad.getId())
                .nombreEntidad(entidad.getNombreEntidad())
                .dominioCorreo(entidad.getDominioCorreo())
                .build();
    }
}