package com.project.camunda.delegate;


import com.project.entity.FlujoCorreos;
import com.project.enums.ETAPA;
import com.project.service.FlujoCorreoService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("registrarInicioAprobacion")
public class RegistrarInicioAprobacion implements JavaDelegate {

    @Autowired
    private FlujoCorreoService flujoCorreoService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String correoId = (String) delegateExecution.getVariable("correoId");
        String correoAprobador = (String) delegateExecution.getVariable("correoAprobador");
        LocalDateTime fechaAsignacionGestor  = (LocalDateTime) delegateExecution.getVariable("fechaAsignacionAprobador");

        FlujoCorreos flujoCorreo = flujoCorreoService.iniciarFlujo(correoId, correoAprobador, ETAPA.APROBACION, fechaAsignacionGestor);

        delegateExecution.setVariable("flujoAprobacionId", flujoCorreo.getId());
    }
}
