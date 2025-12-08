package com.project.service;

import com.project.entity.Correo;

import java.time.LocalDateTime;


public interface CorreoService {

    Correo registrarNuevoCorreo(Correo correo);

    void registrarEnvioFinal(String correoId, LocalDateTime fechaRespuesta);

}
