package com.project.entity;

import com.project.enums.ETAPA;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "flujo_correos")
public class FlujoCorreos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "correo_id")
    private Correo correo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private ETAPA etapa;

    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaFinalizacion;
}
