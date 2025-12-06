package com.project.entity;


import jakarta.persistence.*;
import lombok.Data;
import com.project.enums.ROL;



@Data
@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private ROL nombreRol;
}