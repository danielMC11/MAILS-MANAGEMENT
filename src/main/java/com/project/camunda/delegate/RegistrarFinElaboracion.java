package com.project.camunda.delegate;

import com.project.entity.FlujoCorreos;
import com.project.service.FlujoCorreoService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service("registrarFinElaboracion")
public class RegistrarFinElaboracion implements JavaDelegate {

    @Autowired
    private FlujoCorreoService flujoCorreoService;


    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        Long flujoElaboracionId = (Long) delegateExecution.getVariable("flujoElaboracionId");
        LocalDateTime fechaFinalizacionGestor =  (LocalDateTime) delegateExecution.getVariable("fechaFinalizacionGestor");

        flujoCorreoService.terminarFlujo(flujoElaboracionId, fechaFinalizacionGestor);

    }


}
