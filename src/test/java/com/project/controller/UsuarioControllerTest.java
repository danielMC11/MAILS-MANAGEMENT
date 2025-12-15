package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.UsuarioActualizarRequest;
import com.project.dto.UsuarioCrearRequest;
import com.project.dto.UsuarioResponse;
import com.project.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void crearUsuario() throws Exception {
        UsuarioCrearRequest request = new UsuarioCrearRequest();
        request.setNombres("Test");
        request.setApellidos("User");
        request.setNumeroCelular("3101234567");
        request.setCorreo("test@test.com");
        request.setPassword("password");

        UsuarioResponse response = new UsuarioResponse();
        response.setId(1L);
        response.setNombres("Test");
        response.setCorreo("test@test.com");

        when(usuarioService.crearUsuario(any(UsuarioCrearRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/usuarios/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombres").value("Test"));
    }

    @Test
    void eliminarUsuario() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/eliminar/1"))
                .andExpect(status().isOk());
    }

    @Test
    void reactivarUsuario() throws Exception {
        mockMvc.perform(put("/api/v1/usuarios/reactivar/1"))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarUsuario() throws Exception {
        UsuarioActualizarRequest request = new UsuarioActualizarRequest();
        request.setNombres("Test");
        request.setApellidos("User");
        request.setNumeroCelular("3101234567");
        request.setCorreo("test@test.com");

        UsuarioResponse response = new UsuarioResponse();
        response.setId(1L);
        response.setNombres("Test");

        when(usuarioService.actualizarUsuario(any(Long.class), any(UsuarioActualizarRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/usuarios/actualizar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombres").value("Test"));
    }

    @Test
    void cambiarEstado() throws Exception {
        mockMvc.perform(put("/api/v1/usuarios/1/estado")
                .param("activo", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void listarTodosUsuarios() throws Exception {
        when(usuarioService.listarTodosUsuarios()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/usuarios/"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerUsuarioPorId() throws Exception {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(1L);
        response.setNombres("Test");

        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombres").value("Test"));
    }
}
