package com.project.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cuentas")
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String nombreCuenta;

    @Column(length = 64)
    private String correoCuenta;

    @ManyToOne
    @JoinColumn(name = "entidad_id")
    private Entidad entidad;
}