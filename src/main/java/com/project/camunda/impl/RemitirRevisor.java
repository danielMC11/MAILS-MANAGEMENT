package com.project.camunda.impl;

import com.project.camunda.MailProcessor;
import com.project.camunda.delegate.Util;
import com.project.entity.Usuario;
import com.project.enums.ROL;
import com.project.mails.Mail;
import com.project.repository.UsuarioRepository;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class RemitirRevisor implements MailProcessor {

    public static String ACTIVITY_ID = "remitirRevisor";


    @Autowired
    private TaskService taskService;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public boolean supports(Mail mail) {
        String correoFrom = Util.getCorreoCompleto(mail.getFrom());
        String correoTo = Util.getCorreoCompleto(mail.getTo());

        Usuario usuarioFrom = usuarioRepository.findByCorreo(correoFrom).orElse(null);
        Usuario usuarioTo =  usuarioRepository.findByCorreo(correoTo).orElse(null);


        if(usuarioFrom != null &&  usuarioTo != null) {
            boolean esGestor = usuarioFrom.getRoles().stream().anyMatch(
                    rol -> rol.getNombreRol() == ROL.GESTOR
            );

            boolean esRevisor = usuarioTo.getRoles().stream().anyMatch(
                    rol -> rol.getNombreRol() == ROL.REVISOR
            );

            return esGestor && esRevisor;

        }

        return false;
    }

    @Override
    public void process(Mail mail) {
        String businessKey = mail.getOriginalMessageId();

        Task task = taskService.createTaskQuery()
                .processInstanceBusinessKey(businessKey)
                .taskDefinitionKey(RemitirRevisor.ACTIVITY_ID)
                .singleResult();

        if (task != null) {
            Map<String,Object> variables = new HashMap<>();
            variables.put("correoRevisor", Util.getCorreoCompleto(mail.getTo()));
            variables.put("fechaAsignacionRevisor", Util.convertirDateALocalDatetime(mail.getReceivedDate()));

            taskService.complete(task.getId(), variables);
        }
    }
}
