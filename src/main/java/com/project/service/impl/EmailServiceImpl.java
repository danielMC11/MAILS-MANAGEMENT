package com.project.service.impl;

import com.project.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.mail.javamail.JavaMailSender;



@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    private final TemplateEngine templateEngine;


    public EmailServiceImpl(JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void enviarRecordatorio(String correoGestor, String fechaVencimiento, String radicadoEntrada,
                                                     String asunto, String gestionId, String subject, String plantilla) {
        try {
            MimeMessage message = emailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Map<String, Object> model = new HashMap<>();
            model.put("fechaVencimiento", fechaVencimiento);
            model.put("radicadoEntrada", radicadoEntrada);
            model.put("asunto", asunto);
            model.put("idMensaje", "rfc822msgid:" + gestionId);


            Context context = new Context();
            context.setVariables(model);
            String html = templateEngine.process("email/"+plantilla, context);

            helper.setTo(correoGestor);
            helper.setSubject(subject);
            helper.setText(html, true);

            emailSender.send(message);
        } catch (Exception e){
            throw new RuntimeException("Error al enviar correo", e);
        }
    }


}
