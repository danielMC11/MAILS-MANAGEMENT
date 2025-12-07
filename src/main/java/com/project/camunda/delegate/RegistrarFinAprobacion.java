package com.project.camunda.delegate;

import com.project.service.FlujoCorreoService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("registrarFinAprobacion")
public class RegistrarFinAprobacion implements JavaDelegate {

    @Autowired
    private FlujoCorreoService flujoCorreoService;


    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long flujoAprobacionId = (Long) delegateExecution.getVariable("flujoAprobacionId");
        LocalDateTime fechaFinalizacionAprobador =  (LocalDateTime) delegateExecution.getVariable("fechaFinalizacionAprobador");

        flujoCorreoService.terminarFlujo(flujoAprobacionId, fechaFinalizacionAprobador);
    }
}
