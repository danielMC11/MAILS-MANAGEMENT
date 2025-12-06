package com.project.camunda.delegate;

import com.project.entity.Correo;
import com.project.entity.Cuenta;
import com.project.enums.ESTADO;
import com.project.repository.CorreoRepository;
import com.project.repository.CuentaRepository;
import com.project.service.CorreoService;
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

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String from = (String) delegateExecution.getVariable("from");
        String subject= (String) delegateExecution.getVariable("subject");
        String text= (String) delegateExecution.getVariable("text");
        Date date= (Date) delegateExecution.getVariable("date");

        ZoneId zonaSistema = ZoneId.systemDefault();

        // 2. Convierte Date a Instant y luego a LocalDate usando la zona
        LocalDateTime localDate = date.toInstant()
                .atZone(zonaSistema)
                .toLocalDateTime();

        Cuenta cuenta = cuentaRepository.findByCorreoCuenta(from).orElseThrow(
                () -> new RuntimeException("Cuenta con correo no encontrada")
        );

        Correo correo = new Correo();
        correo.setIdProceso(delegateExecution.getProcessInstanceId());
        correo.setFechaRecepcion(localDate);
        correo.setAsunto(subject);
        correo.setCuerpoTexto(text);
        correo.setEstado(ESTADO.PENDIENTE);
        correo.setCuenta(cuenta);

        correoService.registrarNuevoCorreo(correo);

    }


}
