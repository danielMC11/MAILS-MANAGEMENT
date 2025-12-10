package com.project.camunda.impl;

import com.project.camunda.MailProcessor;
import com.project.camunda.delegate.Util;
import com.project.entity.Usuario;
import com.project.enums.ETAPA;
import com.project.enums.ROL;
import com.project.mails.Mail;
import com.project.repository.CorreoRepository;
import com.project.repository.UsuarioRepository;
import com.project.service.CorreoService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class RemitirGestor implements MailProcessor {


    public static String ACTIVITY_ID = "remitirGestor";

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CorreoService correoService;

    @Override
    public boolean supports(Mail mail) {

        String correoFrom = Util.getCorreoCompleto(mail.getFrom());
        String correoTo = Util.getCorreoCompleto(mail.getTo());
        String businessKey = mail.getOriginalMessageId();


        Usuario usuarioFrom = usuarioRepository.findByCorreo(correoFrom).orElse(null);
        Usuario usuarioTo =  usuarioRepository.findByCorreo(correoTo).orElse(null);


        if(usuarioFrom != null &&  usuarioTo != null) {
            boolean esIntegrador = usuarioFrom.getRoles().stream().anyMatch(
                    rol -> rol.getNombreRol() == ROL.INTEGRADOR
            );

            boolean esGestor = usuarioTo.getRoles().stream().anyMatch(
                    rol -> rol.getNombreRol() == ROL.GESTOR
            );



            String activeProcessInstanceId = Util.getActiveProcessInstanceId(runtimeService, businessKey);


            List<String> activityIds = runtimeService.getActiveActivityIds(activeProcessInstanceId);
                boolean enActividad = activityIds.contains(ACTIVITY_ID);

                ETAPA etapaActual = (ETAPA) runtimeService.getVariable(activeProcessInstanceId, "etapaActual");


                return esIntegrador && esGestor && enActividad && etapaActual == ETAPA.RECEPCION;


        }

        return false;
    }

    @Override
    public void process(Mail mail) {

        String businessKey = mail.getOriginalMessageId();

        String idGestion = mail.getMessageId();
        correoService.ingresarGestionId(businessKey, idGestion);

        String text = mail.getText();

        String tipoSolicitudNombre = "GENERAL";

        Pattern patternTS = Pattern.compile("\\[TIPO-SOLICITUD-(.+)\\]");
        Matcher matcherTS = patternTS.matcher(text);

        if (matcherTS.find()) {
            tipoSolicitudNombre = matcherTS.group(1).replace("*", "").trim();
        }

        String nivelUrgencia = "MEDIA";

        Pattern patternNU = Pattern.compile("\\[NIVEL-URGENCIA-(.+)\\]");
        Matcher matcherNU = patternNU.matcher(text);

        if (matcherNU.find()) {
            nivelUrgencia = matcherNU.group(1).replace("*", "").trim();
        }

        String radicadoEntrada = null;

        Pattern patternRE = Pattern.compile("\\[RADICADO-ENTRADA-(\\d+)\\]");
        Matcher matcherRE = patternRE.matcher(text);

        if (matcherRE.find()) {
            radicadoEntrada = matcherRE.group(1);
        }

        int diasPlazo = 0;
        LocalDateTime fechaLimiteRespuesta = null; // Esta será la fecha de vencimiento

        Pattern patternPR = Pattern.compile("\\[PLAZO-RESPUESTA-(\\d+)\\]");
        Matcher matcherPR = patternPR.matcher(text);

        if (matcherPR.find()) {
            String diasStr = matcherPR.group(1);

            try {
                diasPlazo = Integer.parseInt(diasStr);

                // Si se encontró [PR-X] y X es >= 3, establece la fecha límite
                if (diasPlazo >= 3) {
                    fechaLimiteRespuesta = LocalDateTime.now().plusDays(diasPlazo).plusMinutes(3);
                }

            } catch (NumberFormatException e) {
                System.err.println("Error al parsear días de plazo: " + diasStr);
            }
        }

        if (fechaLimiteRespuesta == null) {
            // Si no se encontró un patrón válido o el plazo era menor a 3 días,
            // se establece la fecha límite a HOY + 3 días.
            fechaLimiteRespuesta = LocalDateTime.now().plusDays(3).plusMinutes(3);
        }

        LocalDateTime fechaAlerta = fechaLimiteRespuesta.minusDays(3);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        String fechaVencimientoStr = fechaLimiteRespuesta.format(formatter);
        String fechaAlertaStr = fechaAlerta.format(formatter);

        String activeProcessInstanceId = Util.getActiveProcessInstanceId(runtimeService, businessKey);

        Task task = taskService.createTaskQuery()
                .processInstanceId(activeProcessInstanceId)
                .singleResult();

        if (task != null) {
            Map<String,Object> variables = new HashMap<>();
            variables.put("radicadoEntrada", radicadoEntrada);
            variables.put("tipoSolicitudNombre", tipoSolicitudNombre);
            variables.put("nivelUrgencia", nivelUrgencia);
            variables.put("plazoRespuestaEnDias", diasPlazo);
            variables.put("fechaAlerta", fechaAlertaStr);
            variables.put("fechaVencimiento", fechaVencimientoStr);
            variables.put("correoGestor", Util.getCorreoCompleto(mail.getTo()));
            variables.put("fechaAsignacionGestor", Util.convertirDateALocalDatetime(mail.getReceivedDate()));
            variables.put("gestionId", idGestion);

            taskService.complete(task.getId(), variables);
        }


    }
}
