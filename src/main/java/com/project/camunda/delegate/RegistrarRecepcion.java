package com.project.camunda.delegate;

import com.project.entity.Correo;
import com.project.entity.Cuenta;
import com.project.entity.FlujoCorreos;
import com.project.enums.ESTADO;
import com.project.enums.ETAPA;
import com.project.repository.CorreoRepository;
import com.project.repository.CuentaRepository;
import com.project.repository.FlujoCorreoRepository;
import com.project.service.CorreoService;
import com.project.service.FlujoCorreoService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Service("registrarRecepcion")
public class RegistrarRecepcion implements JavaDelegate {


    @Autowired
    private CorreoService correoService;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private FlujoCorreoService flujoCorreoService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String from = (String) delegateExecution.getVariable("from");
        String subject= (String) delegateExecution.getVariable("subject");
        String text= (String) delegateExecution.getVariable("text");
        Date date= (Date) delegateExecution.getVariable("date");
        String correoId= (String) delegateExecution.getVariable("correoId");

        Cuenta cuenta = cuentaRepository.findByCorreoCuenta(from).orElseThrow(
                () -> new RuntimeException("Cuenta con correo no encontrada")
        );

        Correo correo = new Correo();
        correo.setId(correoId);
        correo.setIdProceso(delegateExecution.getProcessInstanceId());
        correo.setFechaRecepcion(Util.convertirDateALocalDatetime(date));
        correo.setAsunto(subject);
        correo.setCuerpoTexto(text);
        correo.setEstado(ESTADO.PENDIENTE);
        correo.setCuenta(cuenta);

        Correo correoGuardado = correoService.registrarNuevoCorreo(correo);


        String correoIntegrador = (String) delegateExecution.getVariable("correoIntegrador");
        FlujoCorreos flujoCorreos = flujoCorreoService.iniciarFlujo(correoGuardado.getId(), correoIntegrador, ETAPA.RECEPCION, Util.convertirDateALocalDatetime(date));

        delegateExecution.setVariable("flujoRecepcionId", flujoCorreos.getId());
        delegateExecution.setVariable("etapaActual", ETAPA.RECEPCION);


    }


}
