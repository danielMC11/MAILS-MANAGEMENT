package com.project.repository;

import com.project.entity.Entidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntidadRepository extends JpaRepository<Entidad, Long> {

    Optional<Entidad> findByDominioCorreo(String dominioCorreo);

    boolean existsByDominioCorreo(String dominioCorreo);

    List<Entidad> findByNombreEntidadContainingIgnoreCase(String nombreEntidad);
}