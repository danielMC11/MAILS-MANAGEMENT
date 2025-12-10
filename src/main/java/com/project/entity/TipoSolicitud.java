package com.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entidad que representa un tipo de solicitud
 * Mapea a la tabla tipo_solicitud en la base de datos
 */
@Entity
@Table(name = "tipo_solicitud")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", length = 50)
    private String nombre;


    public String getNombreTipoSolicitud() {
        return  nombre;
    }
    // Relación uno a muchos con Correos
    @OneToMany(mappedBy = "tipoSolicitud", fetch = FetchType.LAZY)
    private List<Correo> correos;
}