package com.project.service;

import com.project.enums.ETAPA;

import java.time.LocalDateTime;

public interface FlujoCorreoService {

    void iniciarFlujo(String correoId, String correoResponsable, ETAPA etapa, LocalDateTime fechaAsignacion);

    void terminarFlujo(Long flujoId, LocalDateTime fechaFinalizacion);
}
