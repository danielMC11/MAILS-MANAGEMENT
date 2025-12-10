package com.project.repository;

import com.project.entity.TipoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

import java.util.List;
import java.util.Map;



@Repository
public interface TipoSolicitudRepository extends JpaRepository<TipoSolicitud, Long> {

    Optional<TipoSolicitud> findByNombre(String nombre);


    /**
     * Obtiene todos los tipos de solicitud con sus estadísticas calculadas
     * desde la tabla correos usando SQL nativo
     */
    @Query(value = """
        SELECT 
            ts.id as tipo_solicitud_id,
            ts.nombre as nombre,
            COUNT(c.correo_id) as total_correos,
            COUNT(CASE WHEN c.estado = 'PENDIENTE' THEN 1 END) as correos_pendientes,
            COUNT(CASE WHEN c.estado = 'RESPONDIDO' THEN 1 END) as correos_respondidos,
            COUNT(CASE WHEN c.estado = 'VENCIDO' THEN 1 END) as correos_vencidos,
            COALESCE(AVG(c.plazo_respuesta_en_dias), 0) as plazo_dias_promedio,
            MIN(c.plazo_respuesta_en_dias) as plazo_dias_minimo,
            MAX(c.plazo_respuesta_en_dias) as plazo_dias_maximo,
            AVG(
                CASE 
                    WHEN c.fecha_respuesta IS NOT NULL AND c.fecha_recepcion IS NOT NULL
                    THEN EXTRACT(EPOCH FROM (c.fecha_respuesta - c.fecha_recepcion))/86400
                END
            ) as tiempo_promedio_respuesta
        FROM tipo_solicitud ts
        LEFT JOIN correos c ON ts.id = c.tipo_solicitud_id
        GROUP BY ts.id, ts.nombre
        ORDER BY ts.nombre
    """, nativeQuery = true)
    List<Map<String, Object>> findAllConEstadisticas();

    /**
     * Obtiene estadísticas detalladas de un tipo de solicitud específico
     * usando SQL nativo
     */
    @Query(value = """
        SELECT 
            ts.id as tipo_solicitud_id,
            ts.nombre as nombre,
            COUNT(c.correo_id) as total_correos,
            COUNT(CASE WHEN c.estado = 'PENDIENTE' THEN 1 END) as correos_pendientes,
            COUNT(CASE WHEN c.estado = 'RESPONDIDO' THEN 1 END) as correos_respondidos,
            COUNT(CASE WHEN c.estado = 'VENCIDO' THEN 1 END) as correos_vencidos,
            COALESCE(AVG(c.plazo_respuesta_en_dias), 0) as plazo_dias_promedio,
            MIN(c.plazo_respuesta_en_dias) as plazo_dias_minimo,
            MAX(c.plazo_respuesta_en_dias) as plazo_dias_maximo,
            AVG(
                CASE 
                    WHEN c.fecha_respuesta IS NOT NULL AND c.fecha_recepcion IS NOT NULL
                    THEN EXTRACT(EPOCH FROM (c.fecha_respuesta - c.fecha_recepcion))/86400
                END
            ) as tiempo_promedio_respuesta,
            COUNT(
                CASE 
                    WHEN c.estado = 'RESPONDIDO' 
                    AND c.fecha_respuesta IS NOT NULL
                    AND c.fecha_recepcion IS NOT NULL
                    AND EXTRACT(EPOCH FROM (c.fecha_respuesta - c.fecha_recepcion))/86400 <= c.plazo_respuesta_en_dias
                    THEN 1
                END
            ) as correos_en_plazo,
            COUNT(
                CASE 
                    WHEN c.estado = 'RESPONDIDO' 
                    AND c.fecha_respuesta IS NOT NULL
                    AND c.fecha_recepcion IS NOT NULL
                    AND EXTRACT(EPOCH FROM (c.fecha_respuesta - c.fecha_recepcion))/86400 > c.plazo_respuesta_en_dias
                    THEN 1
                END
            ) as correos_fuera_de_plazo
        FROM tipo_solicitud ts
        LEFT JOIN correos c ON ts.id = c.tipo_solicitud_id
        WHERE ts.id = :tipoSolicitudId
        GROUP BY ts.id, ts.nombre
    """, nativeQuery = true)
    Map<String, Object> findEstadisticasByTipoSolicitudId(@Param("tipoSolicitudId") Long tipoSolicitudId);
}

