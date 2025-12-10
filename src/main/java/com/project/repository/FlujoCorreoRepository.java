package com.project.repository;

import com.project.entity.Correo;
import com.project.entity.FlujoCorreos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.project.enums.ETAPA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface FlujoCorreoRepository extends JpaRepository<FlujoCorreos, Long>,
        JpaSpecificationExecutor<FlujoCorreos> {

    Optional<FlujoCorreos> findTopByCorreo_IdOrderByFechaAsignacionDesc(String correoId);

    // Consultas básicas
    List<FlujoCorreos> findByCorreoId(String correoId);
    List<FlujoCorreos> findByCorreoIdOrderByFechaAsignacionDesc(String correoId);
    List<FlujoCorreos> findByCorreoIdOrderByFechaAsignacionAsc(String correoId);

    List<FlujoCorreos> findByUsuarioId(Long usuarioId);
    List<FlujoCorreos> findByUsuarioIdAndFechaFinalizacionIsNull(Long usuarioId);

    List<FlujoCorreos> findByEtapa(ETAPA etapa);
    List<FlujoCorreos> findByEtapaAndFechaFinalizacionIsNull(ETAPA etapa);

    // Consultas por estado
    List<FlujoCorreos> findByFechaFinalizacionIsNull();
    List<FlujoCorreos> findByFechaFinalizacionIsNotNull();
    List<FlujoCorreos> findByUsuarioIsNull();

    // Consultas especiales
    @Query("SELECT f FROM FlujoCorreos f WHERE f.fechaFinalizacion IS NOT NULL " +
            "ORDER BY f.fechaFinalizacion DESC")
    List<FlujoCorreos> findTopNByFechaFinalizacionIsNotNullOrderByFechaFinalizacionDesc(@Param("limit") int limit);

    @Query("SELECT f FROM FlujoCorreos f WHERE f.fechaFinalizacion IS NOT NULL " +
            "ORDER BY (f.fechaFinalizacion - f.fechaAsignacion) DESC")
    List<FlujoCorreos> findTopNByFechaFinalizacionIsNotNullOrderByDuracionDesc(@Param("limit") int limit);

    // Conteos para estadísticas
    Long countByFechaFinalizacionIsNull();
    Long countByFechaFinalizacionIsNotNull();
    Long countByEtapa(ETAPA etapa);

    // Estadísticas por usuario
    @Query("SELECT CONCAT(u.nombres, ' ', u.apellidos), COUNT(f) " +
            "FROM FlujoCorreos f LEFT JOIN f.usuario u " +
            "GROUP BY CONCAT(u.nombres, ' ', u.apellidos)")
    List<Object[]> countFlujosPorUsuario();

    // Estadísticas por entidad
    @Query("SELECT e.nombreEntidad, COUNT(f) " +
            "FROM FlujoCorreos f " +
            "JOIN f.correo c " +
            "JOIN c.cuenta cu " +
            "JOIN cu.entidad e " +
            "GROUP BY e.nombreEntidad")
    List<Object[]> countFlujosPorEntidad();

    // Consultas paginadas
    Page<FlujoCorreos> findAll(org.springframework.data.jpa.domain.Specification<FlujoCorreos> spec, Pageable pageable);

    @Query("SELECT f FROM FlujoCorreos f WHERE f.correo = :correo ORDER BY f.fechaAsignacion DESC")
    List<FlujoCorreos> findByCorreoOrderByFechaAsignacionDesc(@Param("correo") Correo correo);
}