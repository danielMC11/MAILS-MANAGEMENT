package com.project.mails;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Representa un correo electrónico simplificado con texto, html y adjuntos.
 */
public class Mail implements Serializable {

    private static final long serialVersionUID = 1L;

    private String from;
    private String to;
    private String cc;

    private String subject;
    private Date sentDate;
    private Date receivedDate;

    private int messageNumber;
    private String messageId;

    private String text;
    private String html;

    private final List<Attachment> attachments = new ArrayList<>();

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getCc() { return cc; }
    public String getSubject() { return subject; }
    public Date getSentDate() { return sentDate; }
    public Date getReceivedDate() { return receivedDate; }
    public int getMessageNumber() { return messageNumber; }
    public String getMessageId() { return messageId; }
    public String getText() { return text; }
    public String getHtml() { return html; }
    public List<Attachment> getAttachments() { return attachments; }

    /**
     * Convierte un jakarta.mail.Message en un objeto Mail.
     */
    public static Mail from(Message message) throws Exception {
        Mail mail = new Mail();

        mail.from = InternetAddress.toString(message.getFrom());
        mail.to = InternetAddress.toString(message.getRecipients(RecipientType.TO));
        mail.cc = InternetAddress.toString(message.getRecipients(RecipientType.CC));

        mail.subject = message.getSubject();
        mail.sentDate = message.getSentDate();
        mail.receivedDate = message.getReceivedDate();
        mail.messageNumber = message.getMessageNumber();

        if (message instanceof MimeMessage mimeMessage) {
            mail.messageId = mimeMessage.getMessageID();
        }

        processMessageContent(message, mail);
        return mail;
    }

    private static void processMessageContent(Part part, Mail mail)
            throws Exception {

        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int numberOfParts = multipart.getCount();
            for (int i = 0; i < numberOfParts; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                processMessageContent(bodyPart, mail); // recursivo
            }
        } else {
            processMessagePartContent(part, mail);
        }
    }

    private static void processMessagePartContent(Part part, Mail mail)
            throws Exception {

        String disposition = part.getDisposition();

        if (disposition != null && Part.ATTACHMENT.equalsIgnoreCase(disposition)) {
            Attachment attachment = Attachment.from(part);
            mail.attachments.add(attachment);
        } else {
            if (part.isMimeType("text/plain")) {
                // concatenar si hay varias partes de texto
                String content = (String) part.getContent();
                mail.text = (mail.text == null) ? content : mail.text + "\n" + content;
            } else if (part.isMimeType("text/html")) {
                String content = (String) part.getContent();
                mail.html = (mail.html == null) ? content : mail.html + "\n" + content;
            }
        }
    }

    /**
     * Descarga todos los adjuntos en una carpeta única por cada correo.
     */
    /*
    public void downloadAttachments(final String attachmentPath)
            throws IOException, MessagingException {
        if (!attachments.isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            Path downloadPath = Paths.get(attachmentPath, uuid);
            Files.createDirectories(downloadPath);

            for (Attachment attachment : attachments) {
                attachment.download(downloadPath);
            }
        }
    }*/

    @Override
    public String toString() {
        return "Mail [from=" + from +
                ", to=" + to +
                ", cc=" + cc +
                ", subject=" + subject +
                ", sentDate=" + sentDate +
                ", receivedDate=" + receivedDate +
                ", messageNumber=" + messageNumber +
                ", messageId=" + messageId +
                ", text=" + (text != null ? text.substring(0, Math.min(50, text.length())) : null) +
                ", html=" + (html != null ? html.substring(0, Math.min(50, html.length())) : null) +
                ", attachments=" + attachments + "]";
    }
}
