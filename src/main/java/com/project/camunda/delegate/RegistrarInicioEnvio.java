package com.project.camunda.delegate;

import com.project.entity.FlujoCorreos;
import com.project.enums.ETAPA;
import com.project.service.FlujoCorreoService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("registrarInicioEnvio")
public class RegistrarInicioEnvio implements JavaDelegate {

    @Autowired
    private FlujoCorreoService flujoCorreoService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String correoId = (String) delegateExecution.getVariable("correoId");
        String correoIntegrador = (String) delegateExecution.getVariable("correoIntegrador");
        LocalDateTime fechaAsignacionIntegrador  = (LocalDateTime) delegateExecution.getVariable("fechaFinalizacionGestor");

        FlujoCorreos flujoCorreo = flujoCorreoService.iniciarFlujo(correoId, correoIntegrador, ETAPA.ENVIO, fechaAsignacionIntegrador);

        delegateExecution.setVariable("flujoEnvioId", flujoCorreo.getId());
        delegateExecution.setVariable("etapaActual", ETAPA.ENVIO);


    }



}
