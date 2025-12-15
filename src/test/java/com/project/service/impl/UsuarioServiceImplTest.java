package com.project.service.impl;

import com.project.dto.UsuarioActualizarRequest;
import com.project.dto.UsuarioCrearRequest;
import com.project.dto.UsuarioResponse;
import com.project.entity.Rol;
import com.project.entity.Usuario;
import com.project.enums.ROL;
import com.project.repository.RolRepository;
import com.project.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioCrearRequest usuarioCrearRequest;
    private UsuarioActualizarRequest usuarioActualizarRequest;
    private Rol rol;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioCrearRequest = new UsuarioCrearRequest();
        usuarioCrearRequest.setNombres("Test");
        usuarioCrearRequest.setApellidos("User");
        usuarioCrearRequest.setNumeroCelular("3101234567");
        usuarioCrearRequest.setCorreo("test@test.com");
        usuarioCrearRequest.setPassword("password");
        usuarioCrearRequest.setRoles(Set.of(ROL.INTEGRADOR));

        usuarioActualizarRequest = new UsuarioActualizarRequest();
        usuarioActualizarRequest.setNombres("Test");
        usuarioActualizarRequest.setApellidos("User");
        usuarioActualizarRequest.setNumeroCelular("3101234567");
        usuarioActualizarRequest.setCorreo("test@test.com");
        usuarioActualizarRequest.setRoles(Set.of(ROL.INTEGRADOR));

        rol = new Rol();
        rol.setId(1L);
        rol.setNombreRol(ROL.INTEGRADOR);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombres("Test");
        usuario.setApellidos("User");
        usuario.setNumeroCelular("3101234567");
        usuario.setCorreo("test@test.com");
        usuario.setPassword("encodedPassword");
        usuario.setRoles(Set.of(rol));
    }

    @Test
    void crearUsuario() {
        when(rolRepository.findByNombreRol(ROL.INTEGRADOR)).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponse usuarioResponse = usuarioService.crearUsuario(usuarioCrearRequest);

        assertNotNull(usuarioResponse);
        assertEquals("Test", usuarioResponse.getNombres());
        assertEquals("test@test.com", usuarioResponse.getCorreo());
    }

    @Test
    void actualizarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findByNombreRol(ROL.INTEGRADOR)).thenReturn(Optional.of(rol));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponse usuarioResponse = usuarioService.actualizarUsuario(1L, usuarioActualizarRequest);

        assertNotNull(usuarioResponse);
        assertEquals("Test", usuarioResponse.getNombres());
    }

    @Test
    void eliminarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository).save(usuario);
        assertFalse(usuario.getActivo());
    }

    @Test
    void reactivarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.reactivarUsuario(1L);

        verify(usuarioRepository).save(usuario);
        assertTrue(usuario.getActivo());
    }

    @Test
    void obtenerUsuarioPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponse usuarioResponse = usuarioService.obtenerUsuarioPorId(1L);

        assertNotNull(usuarioResponse);
        assertEquals(1L, usuarioResponse.getId());
    }

    @Test
    void listarTodosUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(usuario));

        var result = usuarioService.listarTodosUsuarios();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
