package com.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.project.enums.ESTADO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "correos")
public class Correo {
    @Id
    private String idProceso;

    private String idMensaje;

    @Column(length = 100)
    private String asunto;

    @Column(columnDefinition = "TEXT")
    private String cuerpoTexto;

    @Enumerated(EnumType.STRING)
    private ESTADO estado;

    private LocalDateTime fechaRecepcion;

    private LocalDateTime fechaRespuesta;

    private Integer plazoRespuestaEnDias;


    @Column(length = 50, unique = true)
    private String radicadoEntrada;

    @Column(length = 50, unique = true)
    private String radicadoSalida;

    @ManyToOne
    @JoinColumn(name = "cuenta_id")
    private Cuenta cuenta;

    @ManyToOne
    @JoinColumn(name = "tipo_solicitud_id")
    private TipoSolicitud tipoSolicitud;
}