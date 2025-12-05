package com.project.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dependencias")
public class Dependencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String nombreDependencia;

    @Column(length = 64)
    private String correoDependencia;

    @ManyToOne
    @JoinColumn(name = "entidad_id")
    private Entidad entidad;
}