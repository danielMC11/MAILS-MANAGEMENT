package com.project.service;

import com.project.entity.FlujoCorreos;
import com.project.enums.ETAPA;

import java.time.LocalDateTime;

public interface FlujoCorreoService {

    FlujoCorreos iniciarFlujo(String correoId, String correoResponsable, ETAPA etapa, LocalDateTime fechaAsignacion);

    FlujoCorreos terminarFlujo(Long flujoId, LocalDateTime fechaFinalizacion);


}
