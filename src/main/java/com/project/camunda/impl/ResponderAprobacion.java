package com.project.camunda.impl;

import com.project.camunda.MailProcessor;
import com.project.camunda.delegate.Util;
import com.project.entity.Usuario;
import com.project.enums.ROL;
import com.project.mails.Mail;
import com.project.repository.UsuarioRepository;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ResponderAprobacion implements MailProcessor {

    public static String ACTIVITY_ID = "responderAprobacion";

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public boolean supports(Mail mail) {
        String correoFrom = Util.getCorreoCompleto(mail.getFrom());
        String correoTo = Util.getCorreoCompleto(mail.getTo());
        String businessKey = mail.getOriginalMessageId();


        Usuario usuarioFrom = usuarioRepository.findByCorreo(correoFrom).orElse(null);
        Usuario usuarioTo =  usuarioRepository.findByCorreo(correoTo).orElse(null);


        if(usuarioFrom != null &&  usuarioTo != null) {
            boolean esAprobador = usuarioFrom.getRoles().stream().anyMatch(
                    rol -> rol.getNombreRol() == ROL.APROBADOR
            );

            boolean esGestor = usuarioTo.getRoles().stream().anyMatch(
                    rol -> rol.getNombreRol() == ROL.GESTOR
            );

            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceBusinessKey(businessKey)
                    .active() // Asegura que esté activa
                    .singleResult();
            List<String> activityIds = runtimeService.getActiveActivityIds(processInstance.getId());
            boolean enActividad = activityIds.contains(ACTIVITY_ID);

            return esAprobador && esGestor && enActividad;

        }

        return false;
    }

    @Override
    public void process(Mail mail) {
        String businessKey = mail.getOriginalMessageId();
        String cuerpoCorreo = mail.getText();
        boolean aprobadoOK = false; // Nuevo flag para R-OK

        // 1. Verificar [R-OK] en el cuerpo
        if (cuerpoCorreo != null) {
            // Patrón directo para buscar [R-OK]
            if (cuerpoCorreo.contains("[R-OK]")) {
                aprobadoOK = true;
            }
        }

        Task task = taskService.createTaskQuery()
                .processInstanceBusinessKey(businessKey)
                .singleResult();

        // Solo procede si la tarea existe y se encontró [R-OK]
        if (task != null && aprobadoOK) {
            Map<String,Object> variables = new HashMap<>();

            variables.put("fechaFinalizacionAprobador", Util.convertirDateALocalDatetime(mail.getReceivedDate()));

            taskService.complete(task.getId(), variables);
        }
    }
}
