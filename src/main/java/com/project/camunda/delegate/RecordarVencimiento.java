package com.project.camunda.delegate;

import com.project.service.CorreoService;
import com.project.service.EmailService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service("recordarVencimiento")

public class RecordarVencimiento implements JavaDelegate {


    @Autowired
    private EmailService emailService;

    @Autowired
    private CorreoService correoService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String correoGestor = (String) delegateExecution.getVariable("correoGestor");
        String fechaVencimiento = (String) delegateExecution.getVariable("fechaVencimiento");
        String radicadoEntrada = (String) delegateExecution.getVariable("radicadoEntrada");
        String asunto =  (String) delegateExecution.getVariable("subject");
        String gestionId = (String) delegateExecution.getVariable("gestionId");


        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Parsear el String ISO a LocalDateTime (Java lo maneja automáticamente)
        LocalDateTime fecha = LocalDateTime.parse(fechaVencimiento);

        // Formatear a String
        String fechaVencimientoFormateada = fecha.format(displayFormatter);

        String correoId = (String) delegateExecution.getVariable("correoId");
        correoService.vencerCorreo(correoId);


        emailService.enviarRecordatorio(
                correoGestor,
                fechaVencimientoFormateada,
                radicadoEntrada,
                asunto,
                gestionId,
                "🚨 RECORDATORIO DE VENCIMIENTO ⏳",
                "vencido"
        );

    }

}
