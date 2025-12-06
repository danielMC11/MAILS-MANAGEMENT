package com.project.camunda.impl;

import com.project.camunda.MailProcessor;
import com.project.camunda.delegate.Util;
import com.project.entity.Usuario;
import com.project.enums.ROL;
import com.project.mails.Mail;
import com.project.repository.UsuarioRepository;
import com.project.service.UsuarioService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class RemitirGestor implements MailProcessor {


    public static String ACTIVITY_ID = "remitirGestor";

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
            boolean esIntegrador = usuarioFrom.getRoles().stream().anyMatch(
                    rol -> rol.getNombreRol() == ROL.INTEGRADOR
            );

            boolean esGestor = usuarioTo.getRoles().stream().anyMatch(
                    rol -> rol.getNombreRol() == ROL.GESTOR
            );

            return esIntegrador && esGestor;

        }

        return false;
    }

    @Override
    public void process(Mail mail) {


        /*
        Pattern pattern = Pattern.compile("\\[RAD-(\\w+)] \\[PR-(\\w+)]");
        Matcher matcher = pattern.matcher(mail.getSubject());

        if (matcher.find()) {
            String radicado = matcher.group(1); // Captura el primer grupo (XXXX)
            String prioridad = matcher.group(2);
        }*/

        String businessKey = mail.getOriginalMessageId();

        Task task = taskService.createTaskQuery()
                .processInstanceBusinessKey(businessKey)
                .taskDefinitionKey(RemitirGestor.ACTIVITY_ID)
                .singleResult();

        if (task == null) {
            // Si no se encuentra la tarea única, lanzamos una excepción.
            String errorMessage = String.format(
                    "No se encontró la tarea activa con BusinessKey: %s en ActivityID: %s",
                    businessKey,
                    RemitirGestor.ACTIVITY_ID
            );
            // Puedes usar RuntimeException o una más específica de tu framework.
            throw new IllegalStateException(errorMessage);
        }

        taskService.complete(task.getId());


    }
}
