package com.project.camunda.delegate;


import com.project.entity.FlujoCorreos;
import com.project.enums.ETAPA;
import com.project.service.FlujoCorreoService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("registrarInicioElaboracion")
public class RegistrarInicioElaboracion implements JavaDelegate {


    @Autowired
    private FlujoCorreoService flujoCorreoService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String correoId = (String) delegateExecution.getVariable("correoId");
        String correoGestor = (String) delegateExecution.getVariable("correoGestor");
        LocalDateTime fechaAsignacionGestor  = (LocalDateTime) delegateExecution.getVariable("fechaAsignacionGestor");

        FlujoCorreos flujoCorreo = flujoCorreoService.iniciarFlujo(correoId, correoGestor, ETAPA.ELABORACION, fechaAsignacionGestor);

        delegateExecution.setVariable("flujoElaboracionId", flujoCorreo.getId());


    }
}
