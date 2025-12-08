package com.project.camunda.impl;

import com.project.camunda.MailProcessor;
import com.project.camunda.delegate.Util;
import com.project.entity.Cuenta;
import com.project.entity.Entidad;
import com.project.entity.Usuario;
import com.project.mails.Mail;
import com.project.repository.CuentaRepository;
import com.project.repository.EntidadRepository;
import com.project.repository.UsuarioRepository;
import com.project.service.CuentaService;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StartMailProcess implements MailProcessor {


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private CuentaService cuentaService;

    @Override
    public void process(Mail mail) {

        String from = mail.getFrom();

        String correoCompleto = Util.getCorreoCompleto(from);

        Cuenta cuenta = cuentaRepository.findByCorreoCuenta(correoCompleto).orElse(null);

        if(cuenta == null) {
            cuentaService.guardarCuenta(from);
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("from", Util.getCorreoCompleto(mail.getFrom()));
        variables.put("correoIntegrador", Util.getCorreoCompleto(mail.getTo()));
        variables.put("subject", mail.getSubject());
        variables.put("text", mail.getText());
        variables.put("date", mail.getReceivedDate());
        variables.put("correoId", mail.getOriginalMessageId());

        runtimeService.startProcessInstanceByKey("mails-management-process",  mail.getOriginalMessageId(),variables);


    }

    @Override
    public boolean supports(Mail mail) {

        String from = mail.getFrom();

        String correoCompleto = Util.getCorreoCompleto(from);

        return usuarioRepository.findByCorreo(correoCompleto).isEmpty();

    }



}
