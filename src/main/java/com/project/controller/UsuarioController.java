package com.project.controller;


import com.project.dto.UsuarioCrearRequest;
import com.project.dto.UsuarioResponse;
import com.project.entity.Usuario;
import com.project.exceptions.ErrorResponse;
import com.project.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios/") // URL base para los recursos de usuario
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("crear")
    public ResponseEntity<?> crearUsuario(
            @Valid @RequestBody UsuarioCrearRequest request) {
            UsuarioResponse usuarioCreado = usuarioService.crearUsuario(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }
}
