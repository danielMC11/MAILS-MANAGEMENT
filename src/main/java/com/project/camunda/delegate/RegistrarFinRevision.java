package com.project.camunda.delegate;


import com.project.service.FlujoCorreoService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("registrarFinRevision")
public class RegistrarFinRevision implements JavaDelegate {


    @Autowired
    private FlujoCorreoService flujoCorreoService;


    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        Long flujoRevisionId = (Long) delegateExecution.getVariable("flujoRevisionId");
        LocalDateTime fechaFinalizacionRevisor =  (LocalDateTime) delegateExecution.getVariable("fechaFinalizacionRevisor");

        flujoCorreoService.terminarFlujo(flujoRevisionId, fechaFinalizacionRevisor);

    }
}
