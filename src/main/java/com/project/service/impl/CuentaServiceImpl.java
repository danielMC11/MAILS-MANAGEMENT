package com.project.service.impl;

import com.project.camunda.delegate.Util;
import com.project.entity.Cuenta;
import com.project.entity.Entidad;
import com.project.repository.CuentaRepository;
import com.project.repository.EntidadRepository;
import com.project.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


}
