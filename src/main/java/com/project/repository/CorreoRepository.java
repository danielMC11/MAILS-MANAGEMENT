package com.project.repository;

import com.project.entity.Correo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorreoRepository extends JpaRepository<Correo,Long> {


    Optional<Correo> findById(String correoId);


}
