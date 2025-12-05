package com.project.mails;

import jakarta.mail.Part;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Representa un adjunto de un correo electrónico.
 */
public class Attachment {

    private final String fileName;

    public Attachment(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * Crea un Attachment a partir de una parte MIME.
     */
    public static Attachment from(Part part) throws Exception {
        String fileName = part.getFileName();
        return new Attachment(fileName);
    }

    @Override
    public String toString() {
        return "Attachment[fileName=" + fileName + "]";
    }
}