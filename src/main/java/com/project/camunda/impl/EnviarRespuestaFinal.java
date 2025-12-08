package com.project.camunda.impl;

import com.project.camunda.MailProcessor;
import com.project.camunda.delegate.Util;
import com.project.entity.Correo;
import com.project.entity.Cuenta;
import com.project.entity.Usuario;
import com.project.enums.ETAPA;
import com.project.enums.ROL;
import com.project.mails.Mail;
import com.project.repository.CorreoRepository;
import com.project.repository.CuentaRepository;
import com.project.repository.UsuarioRepository;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EnviarRespuestaFinal implements MailProcessor {

    public static String ACTIVITY_ID = "enviarRespuestaFinal";


    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private CorreoRepository correoRepository;



    @Override
    public boolean supports(Mail mail) {
        String correoFrom = Util.getCorreoCompleto(mail.getFrom());
        String correoTo = Util.getCorreoCompleto(mail.getTo());
        String businessKey = mail.getOriginalMessageId();


        Usuario usuarioFrom = usuarioRepository.findByCorreo(correoFrom).orElse(null);
        Cuenta cuentaTo = cuentaRepository.findByCorreoCuenta(correoTo).orElse(null);
        Correo correo = correoRepository.findById(businessKey).orElse(null);

        if(usuarioFrom != null &&  cuentaTo != null && correo != null) {

            boolean esIntegrador = usuarioFrom.getRoles().stream().anyMatch(
                    rol -> rol.getNombreRol() == ROL.INTEGRADOR
            );


            String processInstanceId = runtimeService.createProcessInstanceQuery()
                    .processInstanceBusinessKey(businessKey)
                    .active() // Asegura que esté activa
                    .singleResult()
                    .getId();

            List<String> activityIds = runtimeService.getActiveActivityIds(processInstanceId);
            boolean enActividad = activityIds.contains(ACTIVITY_ID);

            ETAPA etapaActual = (ETAPA) runtimeService.getVariable(processInstanceId, "etapaActual");


            return correo.getCuenta().equals(cuentaTo) && esIntegrador && esIntegrador && enActividad && etapaActual == ETAPA.ENVIO;

        }

        return false;
    }

    @Override
    public void process(Mail mail) {
        String businessKey = mail.getOriginalMessageId();

        Task task = taskService.createTaskQuery()
                .processInstanceBusinessKey(businessKey)
                .singleResult();

        if (task != null) {
            Map<String,Object> variables = new HashMap<>();
            variables.put("fechaFinalizacionIntegrador", Util.convertirDateALocalDatetime(mail.getReceivedDate()));
            taskService.complete(task.getId(), variables);
        }
    }
}