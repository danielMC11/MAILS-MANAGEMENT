package com.project.mails;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.event.MessageCountAdapter;
import jakarta.mail.event.MessageCountEvent;
import org.eclipse.angus.mail.imap.IMAPFolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.function.Consumer;

@Service
public class ImapService {

    private final Consumer<Mail> mailHandler;

    @Value("${gmail.address}")
    private String  gmailAddress;

    @Value("${gmail.password}")
    private String  passwod;

    public ImapService(Consumer<Mail> mailHandler) {
        this.mailHandler = mailHandler;
    }

    @PostConstruct
    public void startIdleListener() {
        new Thread(this::runIdleLoop).start();
    }

    private void runIdleLoop() {
        while (true) {
            try {
                listenInbox(); // intenta abrir conexión y entrar en IDLE
            } catch (Exception e) {
                System.err.println("❌ Conexión perdida, reintentando en 30s: " + e.getMessage());
                try {
                    Thread.sleep(30000); // espera 30 segundos antes de reconectar
                } catch (InterruptedException ignored) {}
            }
        }
    }

    private void listenInbox() throws Exception {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");

        Session session = Session.getInstance(props);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", gmailAddress, passwod);
        // ⚠️ Gmail requiere contraseña de aplicación u OAuth2

        IMAPFolder inbox = (IMAPFolder) store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        inbox.addMessageCountListener(new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent event) {
                for (Message msg : event.getMessages()) {
                    try {
                        Mail mail = Mail.from(msg);
                        mailHandler.accept(mail);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        try {

            System.out.println("🔔 Esperando nuevos correos (IDLE)...");
            while (true) {
                inbox.idle(); // bloquea hasta que llegue un correo o se corte la conexión
            }
        } finally {
            inbox.close(false);
            store.close();
        }
    }

}

