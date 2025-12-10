package com.project.service.impl;

import com.project.dto.tipoSolicitud.TipoSolicitudConfigResponse;
import com.project.dto.tipoSolicitud.TipoSolicitudEstadisticasResponse;
import com.project.repository.TipoSolicitudRepository;
import com.project.service.TipoSolicitudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de tipos de solicitud
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class TipoSolicitudServiceImpl implements TipoSolicitudService {

    @Autowired
    private TipoSolicitudRepository tipoSolicitudRepository;

    @Override
    public List<TipoSolicitudConfigResponse> listarTodos() {
        log.info("Listando todos los tipos de solicitud con estadísticas");

        List<Map<String, Object>> resultados = tipoSolicitudRepository.findAllConEstadisticas();

        return resultados.stream()
                .map(this::construirConfigResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TipoSolicitudConfigResponse obtenerPorId(Long id) {
        log.info("Obteniendo tipo de solicitud con id: {}", id);

        Map<String, Object> resultado = tipoSolicitudRepository.findEstadisticasByTipoSolicitudId(id);

        if (resultado == null || resultado.isEmpty()) {
            log.error("Tipo de solicitud no encontrado con id: {}", id);
            throw new IllegalArgumentException("Tipo de solicitud no encontrado con id: " + id);
        }

        return construirConfigResponse(resultado);
    }

    @Override
    public List<TipoSolicitudConfigResponse> listarActivos() {
        log.info("Listando tipos de solicitud activos");

        return listarTodos().stream()
                .filter(TipoSolicitudConfigResponse::getActivo)
                .collect(Collectors.toList());
    }

    @Override
    public TipoSolicitudEstadisticasResponse obtenerEstadisticas(Long id) {
        log.info("Obteniendo estadísticas detalladas del tipo de solicitud con id: {}", id);

        Map<String, Object> resultado = tipoSolicitudRepository.findEstadisticasByTipoSolicitudId(id);

        if (resultado == null || resultado.isEmpty()) {
            log.error("Tipo de solicitud no encontrado con id: {}", id);
            throw new IllegalArgumentException("Tipo de solicitud no encontrado con id: " + id);
        }

        return construirEstadisticasResponse(resultado);
    }

    // ==================== MÉTODOS PRIVADOS PARA CONSTRUIR RESPONSES ====================

    private TipoSolicitudConfigResponse construirConfigResponse(Map<String, Object> resultado) {
        Long totalCorreos = getLong(resultado, "total_correos");
        Long correosPendientes = getLong(resultado, "correos_pendientes");
        Long correosRespondidos = getLong(resultado, "correos_respondidos");
        Long correosVencidos = getLong(resultado, "correos_vencidos");
        Double plazoDiasPromedio = getDouble(resultado, "plazo_dias_promedio");

        return TipoSolicitudConfigResponse.builder()
                .id(getLong(resultado, "tipo_solicitud_id"))
                .nombre(getString(resultado, "nombre"))
                .plazoDiasPromedio(plazoDiasPromedio != null ? plazoDiasPromedio.intValue() : null)
                .plazoDiasMinimo(getInteger(resultado, "plazo_dias_minimo"))
                .plazoDiasMaximo(getInteger(resultado, "plazo_dias_maximo"))
                .urgencia(calcularUrgencia(plazoDiasPromedio))
                .descripcion(generarDescripcion(getString(resultado, "nombre")))
                .activo(totalCorreos > 0)
                .totalCorreos(totalCorreos)
                .correosPendientes(correosPendientes)
                .correosRespondidos(correosRespondidos)
                .correosVencidos(correosVencidos)
                .tiempoPromedioRespuestaReal(getDouble(resultado, "tiempo_promedio_respuesta"))
                .build();
    }

    private TipoSolicitudEstadisticasResponse construirEstadisticasResponse(Map<String, Object> resultado) {
        Long totalCorreos = getLong(resultado, "total_correos");
        Long correosPendientes = getLong(resultado, "correos_pendientes");
        Long correosRespondidos = getLong(resultado, "correos_respondidos");
        Long correosVencidos = getLong(resultado, "correos_vencidos");
        Long correosEnPlazo = getLong(resultado, "correos_en_plazo");
        Long correosFueraDePlazo = getLong(resultado, "correos_fuera_de_plazo");

        Double porcentajePendientes = calcularPorcentaje(correosPendientes, totalCorreos);
        Double porcentajeRespondidos = calcularPorcentaje(correosRespondidos, totalCorreos);
        Double porcentajeVencidos = calcularPorcentaje(correosVencidos, totalCorreos);
        Double porcentajeCumplimiento = calcularPorcentaje(correosEnPlazo, correosEnPlazo + correosFueraDePlazo);

        return TipoSolicitudEstadisticasResponse.builder()
                .tipoSolicitudId(getLong(resultado, "tipo_solicitud_id"))
                .nombre(getString(resultado, "nombre"))
                .totalCorreos(totalCorreos)
                .correosPendientes(correosPendientes)
                .correosRespondidos(correosRespondidos)
                .correosVencidos(correosVencidos)
                .plazoDiasPromedio(getInteger(resultado, "plazo_dias_promedio"))
                .plazoDiasMinimo(getInteger(resultado, "plazo_dias_minimo"))
                .plazoDiasMaximo(getInteger(resultado, "plazo_dias_maximo"))
                .tiempoPromedioRespuesta(getDouble(resultado, "tiempo_promedio_respuesta"))
                .porcentajePendientes(porcentajePendientes)
                .porcentajeRespondidos(porcentajeRespondidos)
                .porcentajeVencidos(porcentajeVencidos)
                .correosEnPlazo(correosEnPlazo)
                .correosFueraDePlazo(correosFueraDePlazo)
                .porcentajeCumplimiento(porcentajeCumplimiento)
                .build();
    }

    // ==================== MÉTODOS DE CÁLCULO ====================

    private String calcularUrgencia(Double plazoDiasPromedio) {
        if (plazoDiasPromedio == null || plazoDiasPromedio == 0) {
            return "MEDIA";
        }

        if (plazoDiasPromedio <= 5) {
            return "ALTA";
        } else if (plazoDiasPromedio <= 15) {
            return "MEDIA";
        } else {
            return "BAJA";
        }
    }

    private String generarDescripcion(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Tipo de solicitud";
        }

        return switch (nombre.toLowerCase().trim()) {
            case "derecho de petición" -> "Petición formal a entidad pública";
            case "tutela" -> "Acción de protección de derechos fundamentales";
            case "consulta" -> "Consulta general a entidad";
            case "requerimiento" -> "Requerimiento específico de información";
            case "notificación" -> "Notificación formal de procedimientos";
            case "queja" -> "Queja sobre servicio o atención recibida";
            case "reclamo" -> "Reclamo formal sobre situación específica";
            case "sugerencia" -> "Sugerencia de mejora o cambio";
            default -> "Solicitud de tipo " + nombre;
        };
    }

    private Double calcularPorcentaje(Long parte, Long total) {
        if (total == null || total == 0) {
            return 0.0;
        }

        if (parte == null) {
            return 0.0;
        }

        BigDecimal resultado = BigDecimal.valueOf(parte)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        return resultado.doubleValue();
    }

    // ==================== MÉTODOS AUXILIARES DE EXTRACCIÓN ====================

    private Long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return 0L;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof BigInteger) return ((BigInteger) value).longValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).longValue();
        if (value instanceof Double) return ((Double) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
        return 0L;
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof Double) return ((Double) value).intValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).intValue();
        if (value instanceof BigInteger) return ((BigInteger) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Double getDouble(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Float) return ((Float) value).doubleValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).doubleValue();
        if (value instanceof Integer) return ((Integer) value).doubleValue();
        if (value instanceof Long) return ((Long) value).doubleValue();
        if (value instanceof BigInteger) return ((BigInteger) value).doubleValue();
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
}