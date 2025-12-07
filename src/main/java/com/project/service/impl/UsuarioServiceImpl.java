package com.project.service.impl;


import com.project.dto.UsuarioActualizarRequest;
import com.project.dto.UsuarioCrearRequest;
import com.project.dto.UsuarioResponse;
import com.project.entity.Rol;
import com.project.entity.Usuario;
import com.project.repository.RolRepository;
import com.project.repository.UsuarioRepository;
import com.project.service.UsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UsuarioResponse crearUsuario(UsuarioCrearRequest request) {

        // 1. **Busca la entidad Rol por su nombre (String)**
        Set<Rol> roles = new HashSet<>();

        request.getRoles().forEach(rolNombre -> {
            Rol rol = rolRepository.findByNombreRol(rolNombre)
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + rolNombre));
            roles.add(rol);
        });

        // 2. **Crea la Entidad Usuario**
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombres(request.getNombres());
        nuevoUsuario.setApellidos(request.getApellidos());
        nuevoUsuario.setNumeroCelular(request.getNumeroCelular());
        nuevoUsuario.setCorreo(request.getCorreo());

        // Cifra la contraseña antes de guardar
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));

        // 3. **Asigna la Entidad Rol al Usuario**
        nuevoUsuario.setRoles(roles);

        // 4. **Guarda el Usuario**
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        return UsuarioResponse.builder()
                .id(usuarioGuardado.getId())
                .nombres(usuarioGuardado.getNombres())
                .apellidos(usuarioGuardado.getApellidos())
                .numeroCelular(usuarioGuardado.getNumeroCelular())
                .correo(usuarioGuardado.getCorreo())
                .roles(roles.stream().map(rol -> rol.getNombreRol().name()).collect(Collectors.toSet()))
                .build();
    }


    @Transactional
    @Override
    public UsuarioResponse actualizarUsuario(Long id, UsuarioActualizarRequest request) {


        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
                () ->  new IllegalArgumentException("Usuario no encontrado: " + id)
        );

        Set<Rol> roles = new HashSet<>();

        request.getRoles().forEach(rolNombre -> {
            Rol rol = rolRepository.findByNombreRol(rolNombre)
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + rolNombre));
            roles.add(rol);
        });


        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setNumeroCelular(request.getNumeroCelular());
        usuario.setCorreo(request.getCorreo());

        usuario.setRoles(roles);

        // 4. **Guarda el Usuario**
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return UsuarioResponse.builder()
                .id(usuarioGuardado.getId())
                .nombres(usuarioGuardado.getNombres())
                .apellidos(usuarioGuardado.getApellidos())
                .numeroCelular(usuarioGuardado.getNumeroCelular())
                .correo(usuarioGuardado.getCorreo())
                .roles(roles.stream().map(rol -> rol.getNombreRol().name()).collect(Collectors.toSet()))
                .build();
    }

    @Override
    public void eliminarUsuario(Long id) {

        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
                () ->  new IllegalArgumentException("Usuario no encontrado: " + id)
        );
        usuarioRepository.delete(usuario);
    }
}
