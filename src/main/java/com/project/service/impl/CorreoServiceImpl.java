package com.project.service.impl;

import com.project.entity.Correo;
import com.project.enums.ESTADO;
import com.project.repository.CorreoRepository;
import com.project.service.CorreoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class CorreoServiceImpl implements CorreoService {


    @Autowired
    private CorreoRepository correoRepository;

    @Override
    public Correo registrarNuevoCorreo(Correo correo) {
        return correoRepository.save(correo);
    }

    @Override
    public void registrarEnvioFinal(String correoId, LocalDateTime fechaRespuesta) {

        Correo correo = correoRepository.findById(correoId).orElseThrow(
                () -> new RuntimeException("Correo no encontrado")
        );


        correo.setEstado(ESTADO.RESPONDIDO);
        correo.setFechaRespuesta(fechaRespuesta);
        correoRepository.save(correo);


    }


    @Override
    public void ingresarRadicadoEntrada(String correoId, String radicadoEntrada) {
        Correo correo = correoRepository.findById(correoId).orElseThrow(
                () -> new RuntimeException("Correo no encontrado")
        );

        correo.setRadicadoEntrada(radicadoEntrada);

        correoRepository.save(correo);
    }

    @Override
    public void ingresarRadicadoSalida(String correoId, String radicadoSalida) {
        Correo correo = correoRepository.findById(correoId).orElseThrow(
                () -> new RuntimeException("Correo no encontrado")
        );

        correo.setRadicadoSalida(radicadoSalida);

        correoRepository.save(correo);
    }


    @Override
    public void establecerPlazoEnDias(String correoId, Integer plazoRespuesta) {
        Correo correo = correoRepository.findById(correoId).orElseThrow(
                () -> new RuntimeException("Correo no encontrado")
        );

        correo.setPlazoRespuestaEnDias(plazoRespuesta);

        correoRepository.save(correo);

    }
}
