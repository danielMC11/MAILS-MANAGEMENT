package com.project.repository;

import com.project.dto.correo.CorreoEstadisticasResponse;
import com.project.entity.Correo;
import com.project.enums.ESTADO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CorreoRepository extends JpaRepository<Correo, String>, JpaSpecificationExecutor<Correo> {

    Optional<Correo> findById(String correoId);

    // Búsquedas básicas
    Optional<Correo> findByRadicadoEntrada(String radicadoEntrada);
    Optional<Correo> findByRadicadoSalida(String radicadoSalida);
    List<Correo> findByAsuntoContainingIgnoreCase(String asunto);
    List<Correo> findByEstado(ESTADO estado);

    // Búsquedas por relaciones
    List<Correo> findByCuentaId(Long cuentaId);
    List<Correo> findByCuentaEntidadId(Long entidadId);
    List<Correo> findByTipoSolicitudId(Long tipoSolicitudId);

    // Consultas especiales - POSTGRESQL NATIVAS
    @Query(value = "SELECT * FROM correos c WHERE c.estado = 'PENDIENTE' AND " +
            "(c.fecha_recepcion + (c.plazo_respuesta_en_dias || ' days')::INTERVAL) < NOW()",
            nativeQuery = true)
    List<Correo> findCorreosVencidos();

    @Query(value = "SELECT * FROM correos c WHERE c.estado = 'PENDIENTE' AND " +
            "(c.fecha_recepcion + (c.plazo_respuesta_en_dias || ' days')::INTERVAL) BETWEEN NOW() AND :fechaLimite",
            nativeQuery = true)
    List<Correo> findCorreosPorVencer(@Param("fechaLimite") LocalDateTime fechaLimite);

    @Query("SELECT c FROM Correo c WHERE c.estado = 'RESPONDIDO' AND " +
            "c.fechaRespuesta >= :fechaLimite")
    List<Correo> findCorreosRespondidosRecientemente(@Param("fechaLimite") LocalDateTime fechaLimite);

    // Consultas paginadas con Specification (usando JpaSpecificationExecutor)
    Page<Correo> findAll(org.springframework.data.jpa.domain.Specification<Correo> spec, Pageable pageable);

    // Estadísticas
    @Query("SELECT COUNT(c) FROM Correo c")
    Long countTotalCorreos();

    @Query("SELECT COUNT(c) FROM Correo c WHERE c.estado = 'PENDIENTE'")
    Long countCorreosPendientes();

    @Query("SELECT COUNT(c) FROM Correo c WHERE c.estado = 'RESPONDIDO'")
    Long countCorreosRespondidos();

    @Query("SELECT COUNT(c) FROM Correo c WHERE c.estado = 'VENCIDO'")
    Long countCorreosVencidos();

    @Query("SELECT c.cuenta.entidad.nombreEntidad, COUNT(c) FROM Correo c " +
            "GROUP BY c.cuenta.entidad.nombreEntidad")
    List<Object[]> countCorreosPorEntidad();

    @Query("SELECT c.tipoSolicitud.nombre, COUNT(c) FROM Correo c " +
            "WHERE c.tipoSolicitud IS NOT NULL " +
            "GROUP BY c.tipoSolicitud.nombre")
    List<Object[]> countCorreosPorTipoSolicitud();

    @Query(value = "SELECT TO_CHAR(c.fecha_recepcion, 'YYYY-MM') as mes, COUNT(*) as total " +
            "FROM correos c " +
            "GROUP BY TO_CHAR(c.fecha_recepcion, 'YYYY-MM') " +
            "ORDER BY mes",
            nativeQuery = true)
    List<Object[]> countCorreosPorMes();

    // Método para obtener estadísticas completas
    default CorreoEstadisticasResponse obtenerEstadisticas() {
        // Esta implementación crearía y llenaría el DTO de estadísticas
        // Se implementaría en una clase separada o con @Query nativo
        return null; // Placeholder
    }

    default CorreoEstadisticasResponse obtenerEstadisticasPorPeriodo(
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return null; // Placeholder
    }
}