package com.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.project.enums.ESTADO;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "correos")
public class Correo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idProceso;

    @Column(length = 100)
    private String asunto;

    @Column(columnDefinition = "TEXT")
    private String cuerpoTexto;

    @Enumerated(EnumType.STRING)
    private ESTADO estado;

    private LocalDate fechaRecepcion;
    private LocalDate fechaRespuesta;
    private Integer plazoRespuestaEnDias;

    @Column(length = 50)
    private String radicadoEntrada;

    @Column(length = 50)
    private String radicadoSalida;

    @ManyToOne
    @JoinColumn(name = "dependencia_id")
    private Dependencia dependencia;

    @ManyToOne
    @JoinColumn(name = "tipo_solicitud_id")
    private TipoSolicitud tipoSolicitud;
}