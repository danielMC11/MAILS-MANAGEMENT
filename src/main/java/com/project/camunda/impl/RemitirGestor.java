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

            if (Util.isBusinessKeyAssociatedWithRoot(runtimeService, businessKey)) {


                String processInstanceId = runtimeService.createProcessInstanceQuery()
                        .processInstanceBusinessKey(businessKey)
                        .active() // Asegura que esté activa
                        .singleResult()
                        .getId();

                List<String> activityIds = runtimeService.getActiveActivityIds(processInstanceId);
                boolean enActividad = activityIds.contains(ACTIVITY_ID);

                ETAPA etapaActual = (ETAPA) runtimeService.getVariable(processInstanceId, "etapaActual");


                return esIntegrador && esGestor && enActividad && etapaActual == ETAPA.RECEPCION;

            }
        }

        return false;
    }

    @Override
    public void process(Mail mail) {

        String businessKey = mail.getOriginalMessageId();
        String text = mail.getText();

        String regex = "\\[R-E-(\\d+)\\]"; // <--- CAMBIO CLAVE

        String radicadoEntrada = null; // Cambiado el nombre de la variable para reflejar que es solo el número

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            radicadoEntrada = matcher.group(1);
        }


        int diasPlazo = 0;
        LocalDateTime fechaLimiteRespuesta = null; // Esta será la fecha de vencimiento

        Pattern patternPR = Pattern.compile("\\[PR-(\\d+)\\]");
        Matcher matcherPR = patternPR.matcher(text);

        if (matcherPR.find()) {
            String diasStr = matcherPR.group(1);
            try {
                diasPlazo = Integer.parseInt(diasStr);
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear días de plazo: " + diasStr);
            }
        }

        if (diasPlazo > 0) {
            fechaLimiteRespuesta = LocalDateTime.now().plusDays(diasPlazo);
        }

        // --- 3. CÁLCULO DE LA FECHA DE ALERTA (VENCIMIENTO - 3 DÍAS) ---

        LocalDateTime fechaAlerta = null;
        if (fechaLimiteRespuesta != null) {
            fechaAlerta = fechaLimiteRespuesta.minusDays(3);
        }

        // --- 4. FORMATO DE FECHAS (Para almacenar en Camunda) ---

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String fechaVencimientoStr = (fechaLimiteRespuesta != null) ? fechaLimiteRespuesta.format(formatter) : null;
        String fechaAlertaStr = (fechaAlerta != null) ? fechaAlerta.format(formatter) : null;


        Task task = taskService.createTaskQuery()
                .processInstanceBusinessKey(businessKey)
                .singleResult();

        if (task != null) {
            Map<String,Object> variables = new HashMap<>();
            variables.put("radicadoEntrada", radicadoEntrada);
            variables.put("plazoRespuestaEnDias", diasPlazo);

            //PRUEBA RECORDATORIO

            LocalDateTime fechaAlertaPrueba = fechaAlerta.plusMinutes(5);
            String fechaAlertaPruebaStr = fechaAlertaPrueba.format(formatter);


            variables.put("fechaAlerta", fechaAlertaPruebaStr);
            variables.put("fechaVencimiento", fechaVencimientoStr);
            variables.put("correoGestor", Util.getCorreoCompleto(mail.getTo()));
            variables.put("fechaAsignacionGestor", Util.convertirDateALocalDatetime(mail.getReceivedDate()));

            taskService.complete(task.getId(), variables);
        }


    }
}
