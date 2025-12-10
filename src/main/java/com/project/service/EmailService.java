package com.project.service;

public interface EmailService {

    void enviarRecordatorio(String correoGestor, String fechaVencimiento, String radicadoEntrada, String asunto, String idMensaje, String subject, String plantilla);

    void sendResetPasswordEmail(String email, String templateName, String token);


}
