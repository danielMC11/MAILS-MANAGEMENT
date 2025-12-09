package com.project.service.impl;


import com.project.dto.UsuarioActualizarRequest;
import com.project.dto.UsuarioCrearRequest;
import com.project.dto.UsuarioResponse;
import com.project.entity.Rol;
import com.project.entity.Usuario;
import com.project.enums.ROL;
import com.project.repository.RolRepository;
import com.project.repository.UsuarioRepository;
import com.project.service.UsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
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

    // ====================  MÉTODOS PARA LISTAR ====================

    @Override
    public List<UsuarioResponse> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UsuarioResponse> listarUsuariosPaginados(Pageable pageable) {
        Page<Usuario> usuariosPage = usuarioRepository.findAll(pageable);
        return usuariosPage.map(this::construirResponse);
    }

    @Override
    public List<UsuarioResponse> buscarUsuariosPorNombre(String nombre) {
        // Buscar por nombres o apellidos que contengan el texto
        List<Usuario> usuarios = usuarioRepository.findAll().stream()
                .filter(usuario ->
                        usuario.getNombres().toLowerCase().contains(nombre.toLowerCase()) ||
                                usuario.getApellidos().toLowerCase().contains(nombre.toLowerCase())
                )
                .collect(Collectors.toList());

        return usuarios.stream()
                .map(this::construirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioResponse> buscarUsuariosPorRol(String rol) {
        try {
            ROL rolEnum = ROL.valueOf(rol.toUpperCase());
            List<Usuario> usuarios = usuarioRepository.findAll().stream()
                    .filter(usuario -> usuario.getRoles().stream()
                            .anyMatch(r -> r.getNombreRol() == rolEnum))
                    .collect(Collectors.toList());

            return usuarios.stream()
                    .map(this::construirResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido: " + rol +
                    ". Roles válidos: INTEGRADOR, GESTOR, REVISOR, APROBADOR");
        }
    }

    @Override
    public UsuarioResponse obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        return construirResponse(usuario);
    }

    @Override
    public void cambiarEstado(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
    }

    // ==================== MÉTODO PRIVADO PARA CONSTRUIR RESPONSE ====================

    private UsuarioResponse construirResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .numeroCelular(usuario.getNumeroCelular())
                .correo(usuario.getCorreo())
                .roles(usuario.getRoles().stream()
                        .map(rol -> rol.getNombreRol().name())
                        .collect(Collectors.toSet()))
                .activo(usuario.getActivo())
                .build();
    }
}
