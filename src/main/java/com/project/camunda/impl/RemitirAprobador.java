package com.project.camunda.impl;

import com.project.camunda.MailProcessor;
import com.project.camunda.delegate.Util;
import com.project.entity.Usuario;
import com.project.enums.ETAPA;
import com.project.enums.ROL;
import com.project.mails.Mail;
import com.project.repository.UsuarioRepository;
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
public class RemitirAprobador implements MailProcessor {

    public static String ACTIVITY_ID = "remitirAprobador";

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
            boolean esGestor = usuarioFrom.getRoles().stream().anyMatch(
                    rol -> rol.getNombreRol() == ROL.GESTOR
            );

            boolean esAprobador = usuarioTo.getRoles().stream().anyMatch(
                    rol -> rol.getNombreRol() == ROL.APROBADOR
            );


                String activeProcessInstanceId = Util.getActiveProcessInstanceId(runtimeService, businessKey);


                List<String> activityIds = runtimeService.getActiveActivityIds(activeProcessInstanceId);
                boolean enActividad = activityIds.contains(ACTIVITY_ID);

                ETAPA etapaActual = (ETAPA) runtimeService.getVariable(activeProcessInstanceId, "etapaActual");


                return esGestor && esAprobador && enActividad && etapaActual == ETAPA.REVISION;



        }

        return false;
    }

    @Override
    public void process(Mail mail) {
        String businessKey = mail.getOriginalMessageId();
        String activeProcessInstanceId = Util.getActiveProcessInstanceId(runtimeService, businessKey);


        Task task = taskService.createTaskQuery()
                .processInstanceId(activeProcessInstanceId)
                .singleResult();

        if (task != null) {
            Map<String,Object> variables = new HashMap<>();
            variables.put("correoAprobador", Util.getCorreoCompleto(mail.getTo()));
            variables.put("fechaAsignacionAprobador", Util.convertirDateALocalDatetime(mail.getReceivedDate()));

            taskService.complete(task.getId(), variables);
        }
    }
}
