// /service/impl/TipoSolicitudServiceImpl.java
package com.project.service.impl;

import com.project.dto.tipoSolicitud.TipoSolicitudRequest;
import com.project.dto.tipoSolicitud.TipoSolicitudResponse;
import com.project.entity.TipoSolicitud;
import com.project.repository.TipoSolicitudRepository;
import com.project.service.TipoSolicitudService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TipoSolicitudServiceImpl implements TipoSolicitudService {

    private final TipoSolicitudRepository tipoSolicitudRepository;

    @Override
    public List<TipoSolicitudResponse> listarTodos() {
        log.info("Listando todos los tipos de solicitud");
        return tipoSolicitudRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TipoSolicitudResponse buscarPorId(Long id) {
        log.info("Buscando tipo de solicitud con id: {}", id);
        TipoSolicitud tipoSolicitud = tipoSolicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de solicitud no encontrado con id: " + id));
        return toResponse(tipoSolicitud);
    }

    @Override
    public TipoSolicitudResponse crearTipoSolicitud(TipoSolicitudRequest request) {
        log.info("Creando nuevo tipo de solicitud: {}", request.getNombre());

        // Validar que no exista un tipo con el mismo nombre
        tipoSolicitudRepository.findByNombre(request.getNombre()).ifPresent(ts -> {
            throw new IllegalArgumentException("Ya existe un tipo de solicitud con el nombre: " + request.getNombre());
        });

        TipoSolicitud newTipoSolicitud = new TipoSolicitud();
        newTipoSolicitud.setNombre(request.getNombre());

        TipoSolicitud savedTipoSolicitud = tipoSolicitudRepository.save(newTipoSolicitud);
        log.info("Tipo de solicitud creado exitosamente con id: {}", savedTipoSolicitud.getId());

        return toResponse(savedTipoSolicitud);
    }

    @Override
    public TipoSolicitudResponse actualizarTipoSolicitud(Long id, TipoSolicitudRequest request) {
        log.info("Actualizando tipo de solicitud con id: {} a nombre: {}", id, request.getNombre());

        // Buscar el tipo existente
        TipoSolicitud tipoSolicitud = tipoSolicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de solicitud no encontrado con id: " + id));

        // Validar que no exista otro tipo con el mismo nombre (excepto este)
        tipoSolicitudRepository.findByNombre(request.getNombre()).ifPresent(ts -> {
            if (!ts.getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe otro tipo de solicitud con el nombre: " + request.getNombre());
            }
        });

        // Actualizar el nombre
        tipoSolicitud.setNombre(request.getNombre());

        TipoSolicitud updatedTipoSolicitud = tipoSolicitudRepository.save(tipoSolicitud);
        log.info("Tipo de solicitud actualizado exitosamente");

        return toResponse(updatedTipoSolicitud);
    }

    @Override
    @Transactional
    public void eliminarTipoSolicitud(Long id) {
        log.info("Eliminando tipo de solicitud con id: {}", id);

        TipoSolicitud tipoSolicitud = tipoSolicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de solicitud no encontrado con id: " + id));

        // Validar que no tenga correos asociados
        if (tipoSolicitud.getCorreos() != null && !tipoSolicitud.getCorreos().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el tipo de solicitud porque está asociado a "
                    + tipoSolicitud.getCorreos().size() + " correo(s).");
        }

        tipoSolicitudRepository.delete(tipoSolicitud);
        log.info("Tipo de solicitud eliminado exitosamente");
    }

    @Override
    public List<TipoSolicitudResponse> buscarPorNombre(String nombre) {
        log.info("Buscando tipos de solicitud por nombre: {}", nombre);
        return tipoSolicitudRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TipoSolicitudResponse toResponse(TipoSolicitud tipoSolicitud) {
        return TipoSolicitudResponse.builder()
                .id(tipoSolicitud.getId())
                .nombre(tipoSolicitud.getNombre())
                .build();
    }
}