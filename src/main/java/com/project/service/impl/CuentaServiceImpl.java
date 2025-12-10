package com.project.service.impl;

import com.project.camunda.delegate.Util;
import com.project.dto.cuenta.CuentaActualizarRequest;
import com.project.dto.cuenta.CuentaCrearRequest;
import com.project.dto.cuenta.CuentaResponse;
import com.project.entity.Cuenta;
import com.project.entity.Entidad;
import com.project.repository.CuentaRepository;
import com.project.repository.EntidadRepository;
import com.project.service.CuentaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CuentaServiceImpl implements CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private EntidadRepository entidadRepository;

    @Override
    public void guardarCuenta(String from) {

        String correoCompleto = Util.getCorreoCompleto(from);

        String nombreAlias = Util.getNombreAlias(from);
        String nombreCuenta = Util.getCuenta(correoCompleto);
        String dominioCorreo = Util.getDominio(correoCompleto);
        String nombreEntidad = Util.getNombreEntidad(dominioCorreo);

        Cuenta cuentaNueva = new Cuenta();
        if (nombreAlias != null && !nombreAlias.isEmpty()) {
                cuentaNueva.setNombreCuenta(nombreAlias);
        } else {
                cuentaNueva.setNombreCuenta(nombreCuenta);
        }
        cuentaNueva.setCorreoCuenta(correoCompleto);

        entidadRepository.findByDominioCorreo(dominioCorreo).ifPresentOrElse(entidad -> {
                        cuentaNueva.setEntidad(entidad);
                        cuentaRepository.save(cuentaNueva);
                    }, () -> {
                        Entidad entidad = new Entidad();
                        entidad.setNombreEntidad(nombreEntidad);
                        entidad.setDominioCorreo(dominioCorreo);

                        Entidad entidadGuardada = entidadRepository.save(entidad);

                        cuentaNueva.setEntidad(entidadGuardada);
                        cuentaRepository.save(cuentaNueva);
                    }
            );
        }

    @Override
    public CuentaResponse crearCuenta(CuentaCrearRequest request) {
    // 1. Validar que la entidad existe
    Entidad entidad = entidadRepository.findById(request.getEntidadId())
            .orElseThrow(() -> new IllegalArgumentException(
                    "Entidad no encontrada con ID: " + request.getEntidadId()
            ));

    // 2. Validar que no exista otra cuenta con el mismo correo
        if (cuentaRepository.existsByCorreoCuenta(request.getCorreoCuenta())) {
        throw new IllegalArgumentException(
                "Ya existe una cuenta con el correo: " + request.getCorreoCuenta()
        );
    }

    // 3. Validar que el dominio del correo coincida con la entidad
    String dominioCorreo = extractDomain(request.getCorreoCuenta());
        if (!dominioCorreo.equals(entidad.getDominioCorreo())) {
        throw new IllegalArgumentException(
                "El dominio del correo '" + dominioCorreo + "' no coincide con el dominio de la entidad '" +
                        entidad.getDominioCorreo() + "'"
        );
    }

    // 4. Crear la cuenta
    Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setNombreCuenta(request.getNombreCuenta());
        nuevaCuenta.setCorreoCuenta(request.getCorreoCuenta());
        nuevaCuenta.setEntidad(entidad);

    // 5. Guardar la cuenta
    Cuenta cuentaGuardada = cuentaRepository.save(nuevaCuenta);

    // 6. Construir y retornar response
    return toCuentaResponse(cuentaGuardada);
}

    @Transactional
    @Override
    public CuentaResponse actualizarCuenta(Long id, CuentaActualizarRequest request) {

        // 1. Buscar la cuenta existente
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con ID: " + id));

        // 2. Validar que la nueva entidad existe
        Entidad nuevaEntidad = entidadRepository.findById(request.getEntidadId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Entidad no encontrada con ID: " + request.getEntidadId()
                ));

        // 3. Si cambia el correo, validar que no exista otro con ese correo
        if (!cuenta.getCorreoCuenta().equals(request.getCorreoCuenta())) {
            if (cuentaRepository.existsByCorreoCuenta(request.getCorreoCuenta())) {
                throw new IllegalArgumentException(
                        "Ya existe una cuenta con el correo: " + request.getCorreoCuenta()
                );
            }
        }

        // 4. Validar que el dominio del correo coincida con la nueva entidad
        String dominioCorreo = extractDomain(request.getCorreoCuenta());
        if (!dominioCorreo.equals(nuevaEntidad.getDominioCorreo())) {
            throw new IllegalArgumentException(
                    "El dominio del correo '" + dominioCorreo + "' no coincide con el dominio de la entidad '" +
                            nuevaEntidad.getDominioCorreo() + "'"
            );
        }

        // 5. Actualizar la cuenta
        cuenta.setNombreCuenta(request.getNombreCuenta());
        cuenta.setCorreoCuenta(request.getCorreoCuenta());
        cuenta.setEntidad(nuevaEntidad);

        // 6. Guardar cambios
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        // 7. Construir y retornar response
        return toCuentaResponse(cuentaActualizada);
    }

    @Override
    public void eliminarCuenta(Long id) {

        // 1. Buscar la cuenta
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con ID: " + id));

        // 2. Validar que no tenga correos asociados (si la tabla correos existe)
        // Nota: Esta validación dependerá de si existe la relación en la BD

        // 3. Eliminar la cuenta
        cuentaRepository.delete(cuenta);
    }

    @Override
    public List<CuentaResponse> listarCuentas() {
        return cuentaRepository.findAllWithEntidad().stream()
                .map(this::toCuentaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CuentaResponse> buscarCuentas(String query) {
        return cuentaRepository.search(query).stream()
                .map(this::toCuentaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long contarCuentasPorEntidad(Long entidadId) {
        return cuentaRepository.countByEntidadId(entidadId);
    }

    // Método helper para extraer dominio de un correo
    private String extractDomain(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Formato de correo inválido: " + email);
        }
        return email.substring(email.indexOf("@") + 1);
    }

    private CuentaResponse toCuentaResponse(Cuenta cuenta) {
        return CuentaResponse.builder()
                .id(cuenta.getId())
                .nombreCuenta(cuenta.getNombreCuenta())
                .correoCuenta(cuenta.getCorreoCuenta())
                .entidadId(cuenta.getEntidad().getId())
                .nombreEntidad(cuenta.getEntidad().getNombreEntidad())
                .build();
    }
}
