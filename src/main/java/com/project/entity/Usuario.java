package com.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;

@Data
@Entity
@SQLDelete(sql = "UPDATE usuarios SET activo = false WHERE id = ?")
//@SQLRestriction("activo = true")
@Table(name = "usuarios")
public class Usuario implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String nombres;

    @Column(length = 50)
    private String apellidos;

    @Column(length = 20)
    private String numeroCelular;

    @Column(length = 64)
    private String correo;

    private String password;

    private Boolean activo = Boolean.TRUE;;

    @CreatedDate
    private LocalDate fechaCreacion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return  new ArrayList<SimpleGrantedAuthority>(
                roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNombreRol().name())).toList()
        );

    }

    @Override
    public String getUsername() {
        return correo;
    }


}