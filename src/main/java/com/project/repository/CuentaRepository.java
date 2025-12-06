package com.project.repository;

import com.project.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CuentaRepository extends JpaRepository<Cuenta,Long> {

    Optional<Cuenta> findByCorreoCuenta(String correoCuenta);
}
