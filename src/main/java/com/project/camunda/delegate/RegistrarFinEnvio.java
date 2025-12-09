package com.project.camunda.delegate;

import com.project.service.CorreoService;
import com.project.service.FlujoCorreoService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service("registrarFinEnvio")
public class RegistrarFinEnvio implements JavaDelegate {

    @Autowired
    private FlujoCorreoService flujoCorreoService;

    @Autowired
    private CorreoService correoService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String correoId = (String) delegateExecution.getVariable("correoId");
        Long flujoEnvioId = (Long) delegateExecution.getVariable("flujoEnvioId");
        LocalDateTime fechaFinalizacionIntegrador =  (LocalDateTime) delegateExecution.getVariable("fechaFinalizacionIntegrador");

        String radicadoSalida = (String) delegateExecution.getVariable("radicadoSalida");
        correoService.ingresarRadicadoSalida(correoId, radicadoSalida);


        flujoCorreoService.terminarFlujo(flujoEnvioId, fechaFinalizacionIntegrador);
        correoService.registrarEnvioFinal(correoId, fechaFinalizacionIntegrador);


    }
}
