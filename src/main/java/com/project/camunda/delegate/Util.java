package com.project.camunda.delegate;


import org.springframework.context.annotation.Configuration;

@Configuration
public class Util {


    public static String getNombreAlias(String emailString) {
        if (emailString == null) return "";

        int inicio = emailString.indexOf('<');

        if (inicio != -1) {
            // El alias es lo que está antes del '<'
            return emailString.substring(0, inicio).trim();
        }
        // Si no hay '<', no hay alias
        return "";
    }

    /**
     * Extrae la Dirección de Correo Electrónico Completa.
     * Ejemplo: De "Juan Pérez <correo@dom.com>" extrae "correo@dom.com".
     */
    public static String getCorreoCompleto(String emailString) {
        if (emailString == null) return "";

        int inicio = emailString.indexOf('<');
        int fin = emailString.indexOf('>');

        if (inicio != -1 && fin != -1 && fin > inicio) {
            // El correo es lo que está dentro de '< >'
            return emailString.substring(inicio + 1, fin).trim();
        }
        // Si no hay el formato '< >', la cadena es el correo
        return emailString.trim();
    }

    // --- Métodos de Extracción Secundarios (dependen del correo completo) ---

    /**
     * Extrae el Buzón o Nombre de Cuenta (Dependencia).
     * Ejemplo: De "daniel.montero@unillanos.edu.co" extrae "daniel.montero".
     */
    public static String getCuenta(String correoCompleto) {
        if (correoCompleto == null || correoCompleto.isEmpty()) return "";

        int arrobaIndex = correoCompleto.indexOf('@');

        if (arrobaIndex != -1) {
            // La dependencia es lo que está antes del '@'
            return correoCompleto.substring(0, arrobaIndex);
        }
        return "";
    }

    /**
     * Extrae el Dominio Completo.
     * Ejemplo: De "daniel.montero@unillanos.edu.co" extrae "unillanos.edu.co".
     */
    public static String getDominio(String correoCompleto) {
        if (correoCompleto == null || correoCompleto.isEmpty()) return "";

        int arrobaIndex = correoCompleto.indexOf('@');

        if (arrobaIndex != -1 && arrobaIndex < correoCompleto.length() - 1) {
            // El dominio es lo que está después del '@'
            return correoCompleto.substring(arrobaIndex + 1);
        }
        return "";
    }

    /**
     * Extrae el Nombre de la Organización/Entidad (primera parte del dominio).
     * Ejemplo: De "unillanos.edu.co" extrae "unillanos".
     */
    public static String getNombreEntidad(String dominio) {
        if (dominio == null || dominio.isEmpty()) return "";

        int primerPuntoIndex = dominio.indexOf('.');

        if (primerPuntoIndex != -1) {
            // La entidad es lo que está antes del primer '.'
            return dominio.substring(0, primerPuntoIndex);
        }
        // Si no hay punto (ej: dominio "localhost"), devuelve el dominio completo
        return dominio;
    }
}
