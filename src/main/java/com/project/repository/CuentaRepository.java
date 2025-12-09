package com.project.repository;

import com.project.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CuentaRepository extends JpaRepository<Cuenta,Long> {

    Optional<Cuenta> findByCorreoCuenta(String correoCuenta);
    boolean existsByEntidadId(Long entidadId);

    Long countByEntidadId(Long entidadId);


    List<Cuenta> findByEntidadId(Long entidadId);

    // Verificar si existe cuenta con este correo
    boolean existsByCorreoCuenta(String correoCuenta);


    // Método para buscar cuentas por parte del nombre
    List<Cuenta> findByNombreCuentaContainingIgnoreCase(String nombre);

    // Método para buscar cuentas por parte del correo
    List<Cuenta> findByCorreoCuentaContainingIgnoreCase(String correo);
}
