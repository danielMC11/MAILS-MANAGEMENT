package com.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "entidades")
public class Entidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_entidad", length = 50)
    private String nombreEntidad;

    @Column(name = "dominio_correo", length = 20)
    private String dominioCorreo;
}