package com.project.camunda.delegate;


import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


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


    public static LocalDateTime convertirDateALocalDatetime(Date date){
        ZoneId zonaSistema = ZoneId.systemDefault();

        // 2. Convierte Date a Instant y luego a LocalDate usando la zona
        return date.toInstant()
                .atZone(zonaSistema)
                .toLocalDateTime();

    }

    public static String getActiveProcessInstanceId(RuntimeService runtimeService, String parentBusinessKey) {
        // 1. Buscar el proceso PADRE
        ProcessInstance parentInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(parentBusinessKey)
                .active()
                .singleResult();

        if (parentInstance == null) {
            return null; // No existe el padre activo
        }

        // 2. Buscar el proceso HIJO vinculado por el ID del padre
        ProcessInstance childInstance = runtimeService.createProcessInstanceQuery()
                .superProcessInstanceId(parentInstance.getId())
                .active()
                .singleResult(); // Ojo: Si hay múltiples subprocesos paralelos, esto lanzará excepción

        if (childInstance != null) {
            return childInstance.getId();
        }

        return parentInstance.getId(); // El padre existe, pero no tiene subprocesos activos
    }

    public static boolean isBusinessKeyAssociatedWithRoot(RuntimeService runtimeService, String businessKey) {

        // Buscamos una instancia de proceso activa que:
        // 1. Tenga el Business Key proporcionado.
        // 2. NO tenga un Super Process Instance ID (lo cual define a la instancia Root).

        if (businessKey == null || runtimeService == null) {
            return false;
        }

        ProcessInstance rootInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .superProcessInstanceId(null) // <--- Filtra estrictamente por la instancia Root
                .active()
                .singleResult();

        // Si singleResult() devuelve un objeto (no null), significa que encontramos la instancia Root activa.
        return rootInstance != null;
    }







}
