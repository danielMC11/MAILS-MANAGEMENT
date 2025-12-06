package com.project.camunda.impl;

import com.project.camunda.MailProcessor;
import com.project.camunda.delegate.Util;
import com.project.entity.Cuenta;
import com.project.entity.Entidad;
import com.project.mails.Mail;
import com.project.repository.CuentaRepository;
import com.project.repository.EntidadRepository;
import com.project.repository.UsuarioRepository;
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
    private EntidadRepository entidadRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Value("${gmail.address}")
    private String gmailAddress;


    @Override
    public void process(Mail mail) {










        Map<String, Object> variables = new HashMap<>();
        variables.put("from", Util.getCorreoCompleto(mail.getFrom()));
        variables.put("to", Util.getCorreoCompleto(mail.getTo()));
        variables.put("subject", mail.getSubject());
        variables.put("text", mail.getText());
        variables.put("date", mail.getReceivedDate());


        runtimeService.startProcessInstanceByKey("mails-management-process", variables);
    }

    @Override
    public boolean supports(Mail mail) {

        String from = mail.getFrom();

        String correoCompleto = Util.getCorreoCompleto(from);

        if(cuentaRepository.findByCorreoCuenta(correoCompleto).isPresent()){
            return true;
        } else if (usuarioRepository.findByCorreo(correoCompleto).isEmpty()) {


            String nombreAlias = Util.getNombreAlias(from);
            String nombreCuenta = Util.getCuenta(correoCompleto);
            String dominioCorreo = Util.getDominio(correoCompleto);
            String nombreEntidad = Util.getNombreEntidad(dominioCorreo);

            Cuenta cuenta = new Cuenta();
            if(nombreAlias != null && !nombreAlias.isEmpty()){
                cuenta.setNombreCuenta(nombreAlias);
            } else{
                cuenta.setNombreCuenta(nombreCuenta);
            }
            cuenta.setCorreoCuenta(correoCompleto);

            entidadRepository.findByDominioCorreo(dominioCorreo).ifPresentOrElse(entidad -> {
                cuenta.setEntidad(entidad);
                cuentaRepository.save(cuenta);
            }, () -> {
                Entidad entidad = new Entidad();
                entidad.setNombreEntidad(nombreEntidad);
                entidad.setDominioCorreo(dominioCorreo);

                Entidad entidadGuardada = entidadRepository.save(entidad);

                cuenta.setEntidad(entidadGuardada);
                cuentaRepository.save(cuenta);
            }
            );

            return true;
        }

        return false;
    }



}
