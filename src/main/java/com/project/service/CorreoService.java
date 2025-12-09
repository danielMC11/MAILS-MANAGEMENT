package com.project.service;

import com.project.entity.Correo;

import java.time.LocalDateTime;


public interface CorreoService {

    Correo registrarNuevoCorreo(Correo correo);

    void registrarEnvioFinal(String correoId, LocalDateTime fechaRespuesta);

    void ingresarRadicadoEntrada(String correoId, String radicadoEntrada);

    void ingresarRadicadoSalida(String correoId, String radicadoSalida);

    void establecerPlazoEnDias(String correoId, Integer plazoRespuesta);

}
