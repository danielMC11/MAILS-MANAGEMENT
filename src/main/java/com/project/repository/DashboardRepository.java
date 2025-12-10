package com.project.repository;

import com.project.entity.Correo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DashboardRepository extends JpaRepository<Correo, Long> {

    // Método para obtener estadísticas principales - CORREGIDO
    @Query(nativeQuery = true, value = """
        SELECT 
            COUNT(*) as total_correos,
            COUNT(CASE WHEN c.estado = 'RESPONDIDO' THEN 1 END) as correos_respondidos,
            COUNT(CASE WHEN c.estado = 'VENCIDO' THEN 1 END) as correos_vencidos,
            COUNT(CASE WHEN c.estado = 'PENDIENTE' THEN 1 END) as correos_pendientes,
            ROUND(
                COUNT(CASE WHEN c.estado = 'RESPONDIDO' THEN 1 END) * 100.0 / NULLIF(COUNT(*), 0), 
                1
            ) as cumplimiento,
            ROUND(
                AVG(
                    CASE 
                        WHEN c.fecha_respuesta IS NOT NULL 
                        -- CORREGIDO: Calcular diferencia en días usando EPOCH
                        THEN (EXTRACT(EPOCH FROM (c.fecha_respuesta - c.fecha_recepcion)) / 86400.0)
                        ELSE NULL 
                    END
                ), 
                3
            ) as tiempo_promedio_respuesta,
            (SELECT COUNT(*) FROM entidades) as total_entidades,
            (SELECT COUNT(*) FROM cuentas) as total_cuentas,
            (SELECT COUNT(*) FROM usuarios) as total_usuarios,
            COUNT(CASE WHEN c.estado IN ('PENDIENTE', 'VENCIDO') THEN 1 END) as solicitudes_activas
        FROM correos c
    """)
    Map<String, Object> obtenerEstadisticasPrincipales();

    // Obtener distribución por estado de correos - CORREGIDO (ya estaba bien)
    @Query(nativeQuery = true, value = """
        SELECT 
            c.estado as estado,
            COUNT(*) as cantidad
        FROM correos c
        GROUP BY c.estado
        ORDER BY cantidad DESC
    """)
    List<Object[]> obtenerDistribucionPorEstado();

    // Obtener distribución por etapa de flujo - CORREGIDO (ya estaba bien)
    @Query(nativeQuery = true, value = """
        SELECT 
            fc.etapa as etapa,
            COUNT(*) as cantidad
        FROM flujo_correos fc
        GROUP BY fc.etapa
        ORDER BY cantidad DESC
    """)
    List<Object[]> obtenerDistribucionPorEtapa();

    // Obtener correos por entidad - CORREGIDO: Cambiar COUNT(c.id) por COUNT(c.correo_id)
    @Query(nativeQuery = true, value = """
        SELECT 
            e.nombre_entidad as entidad,
            COUNT(c.correo_id) as cantidad  -- CORREGIDO AQUÍ
        FROM correos c
        JOIN cuentas cu ON c.cuenta_id = cu.id
        JOIN entidades e ON cu.entidad_id = e.id
        GROUP BY e.nombre_entidad
        ORDER BY cantidad DESC
        LIMIT 10
    """)
    List<Object[]> obtenerCorreosPorEntidad();

    // Estadísticas para el último mes - CORREGIDO
    @Query(nativeQuery = true, value = """
        SELECT 
            COUNT(*) as total_correos_mes,
            COUNT(CASE WHEN c.estado = 'RESPONDIDO' THEN 1 END) as respondidos_mes,
            COUNT(CASE WHEN c.estado = 'VENCIDO' THEN 1 END) as vencidos_mes,
            ROUND(
                COUNT(CASE WHEN c.estado = 'RESPONDIDO' THEN 1 END) * 100.0 / NULLIF(COUNT(*), 0), 
                1
            ) as cumplimiento_mes
        FROM correos c
        WHERE c.fecha_recepcion >= CURRENT_DATE - INTERVAL '30 days'
    """)
    Map<String, Object> obtenerEstadisticasUltimoMes();
}