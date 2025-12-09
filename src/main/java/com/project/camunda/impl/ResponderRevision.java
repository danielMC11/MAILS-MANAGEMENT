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
import java.util.regex.Matcher;

@Component
public class ResponderRevision implements MailProcessor {

    public static String ACTIVITY_ID = "responderRevision";

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
            boolean esRevisor = usuarioFrom.getRoles().stream().anyMatch(
                    rol -> rol.getNombreRol() == ROL.REVISOR
            );

            boolean esGestor = usuarioTo.getRoles().stream().anyMatch(
                    rol -> rol.getNombreRol() == ROL.GESTOR
            );

            if(!Util.isBusinessKeyAssociatedWithRoot(runtimeService, businessKey)) {


                String childInstanceId = Util.getChildProcessInstanceId(runtimeService, businessKey);


                List<String> activityIds = runtimeService.getActiveActivityIds(childInstanceId);
                boolean enActividad = activityIds.contains(ACTIVITY_ID);

                ETAPA etapaActual = (ETAPA) runtimeService.getVariable(childInstanceId, "etapaActual");


                return esRevisor && esGestor && enActividad && etapaActual == ETAPA.REVISION;
            }
        }

        return false;
    }

    @Override
    public void process(Mail mail) {
        String businessKey = mail.getOriginalMessageId();
        String cuerpoCorreo = mail.getText();
        String resultadoRevision = null;

        // 1. Extraer el valor del asunto
        if (cuerpoCorreo != null) {
            Matcher matcher = java.util.regex.Pattern.compile("\\[R-(APROBADO|DESAPROBADO)\\]").matcher(cuerpoCorreo);
            if (matcher.find()) {
                resultadoRevision = matcher.group(1);
            }
        }

        String childInstanceId = Util.getChildProcessInstanceId(runtimeService, businessKey);


        Task task = taskService.createTaskQuery()
                .processInstanceId(childInstanceId)
                .singleResult();

        if (task != null && resultadoRevision != null) {
            Map<String,Object> variables = new HashMap<>();
            // 2. Lógica para devueltoRevision (true/false)
            boolean devuelto = resultadoRevision.equals("DESAPROBADO");
            variables.put("devueltoRevision", devuelto);

            if(!devuelto) {
                variables.put("fechaFinalizacionRevisor", Util.convertirDateALocalDatetime(mail.getReceivedDate()));
            }

            taskService.complete(task.getId(), variables);
        }

    }

}
