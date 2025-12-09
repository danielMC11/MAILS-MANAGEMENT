package com.project.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo_solicitud")
public class TipoSolicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String nombre;

    public String getNombreTipoSolicitud() {
        return  nombre;
    }
}
