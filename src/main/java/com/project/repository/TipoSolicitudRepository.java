package com.project.repository;

import com.project.entity.TipoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TipoSolicitudRepository extends JpaRepository<TipoSolicitud, Long> {

    Optional<TipoSolicitud> findByNombre(String nombre);
}
