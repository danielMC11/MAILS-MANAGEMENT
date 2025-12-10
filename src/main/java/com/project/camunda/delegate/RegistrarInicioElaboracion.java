package com.project.camunda.delegate;


import com.project.entity.FlujoCorreos;
import com.project.enums.ETAPA;
import com.project.service.CorreoService;
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

    @Autowired
    private CorreoService correoService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String correoId = (String) delegateExecution.getVariable("correoId");
        String correoGestor = (String) delegateExecution.getVariable("correoGestor");
        LocalDateTime fechaAsignacionGestor  = (LocalDateTime) delegateExecution.getVariable("fechaAsignacionGestor");

        Long flujoRecepcionId = (Long) delegateExecution.getVariable("flujoRecepcionId");
        flujoCorreoService.terminarFlujo(flujoRecepcionId, fechaAsignacionGestor);


        String radicadoEntrada = (String) delegateExecution.getVariable("radicadoEntrada");
        Integer plazoRespuestaEnDias = (Integer) delegateExecution.getVariable("plazoRespuestaEnDias");
        String tipoSolicitudNombre = (String) delegateExecution.getVariable("tipoSolicitudNombre");
        String nivelUrgencia = (String) delegateExecution.getVariable("nivelUrgencia");

        correoService.ingresarDatosEntrada(correoId, radicadoEntrada, plazoRespuestaEnDias, tipoSolicitudNombre, nivelUrgencia);

        FlujoCorreos flujoCorreo = flujoCorreoService.iniciarFlujo(correoId, correoGestor, ETAPA.ELABORACION, fechaAsignacionGestor);

        delegateExecution.setVariable("flujoElaboracionId", flujoCorreo.getId());
        delegateExecution.setVariable("etapaActual", ETAPA.ELABORACION);


    }
}
