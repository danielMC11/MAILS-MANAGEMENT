package com.project.controller;

import com.project.dto.UsuarioActualizarRequest;
import com.project.dto.UsuarioCrearRequest;
import com.project.dto.UsuarioResponse;
import com.project.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable("id") Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("reactivar/{id}")
    public ResponseEntity<?> reactivarUsuario(@PathVariable("id") Long id) {
        usuarioService.reactivarUsuario(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping("actualizar/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable("id") Long id, @Valid @RequestBody UsuarioActualizarRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, request));
    }

    @PutMapping("{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable("id") Long id,
            @RequestParam("activo") boolean activo) {
        usuarioService.cambiarEstado(id, activo);
        return ResponseEntity.ok().build();
    }

    // ====================  ENDPOINTS PARA LISTAR ====================

    /**
     * GET /api/v1/usuarios/
     * Lista todos los usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodosUsuarios() {
        List<UsuarioResponse> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * GET /api/v1/usuarios/pagina
     * Lista usuarios paginados
     * Parámetros: pagina, tamano, ordenarPor, direccion
     */
    @GetMapping("pagina")
    public ResponseEntity<Page<UsuarioResponse>> listarUsuariosPaginados(
            // Agrega "pagina" dentro de    la anotación
            @RequestParam(name = "pagina", defaultValue = "0") Integer pagina,
            // Agrega "tamano"
            @RequestParam(name = "tamano", defaultValue = "10") Integer tamano,
            // Agrega "ordenarPor"
            @RequestParam(name = "ordenarPor", defaultValue = "id") String ordenarPor,
            // Agrega "direccion"
            @RequestParam(name = "direccion", defaultValue = "ASC") String direccion) {

        Sort sort = direccion.equalsIgnoreCase("DESC")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable pageable = PageRequest.of(pagina, tamano, sort);
        Page<UsuarioResponse> usuarios = usuarioService.listarUsuariosPaginados(pageable);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * GET /api/v1/usuarios/{id}
     * Obtiene un usuario por ID
     */
    @GetMapping("{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(@PathVariable("id") Long id) {
        UsuarioResponse usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * GET /api/v1/usuarios/buscar/nombre?nombre=...
     * Busca usuarios por nombre o apellido
     */
    @GetMapping("buscar/nombre")
    public ResponseEntity<List<UsuarioResponse>> buscarUsuariosPorNombre(
            @RequestParam("nombre") String nombre) {
        List<UsuarioResponse> usuarios = usuarioService.buscarUsuariosPorNombre(nombre);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * GET /api/v1/usuarios/buscar/rol?rol=...
     * Busca usuarios por rol
     * Roles válidos: INTEGRADOR, GESTOR, REVISOR, APROBADOR
     */
    @GetMapping("buscar/rol")
    public ResponseEntity<List<UsuarioResponse>> buscarUsuariosPorRol(
            @RequestParam("rol") String rol) {
        List<UsuarioResponse> usuarios = usuarioService.buscarUsuariosPorRol(rol);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * GET /api/v1/usuarios/rol/{rol}
     * Otra forma de buscar por rol
     */
    @GetMapping("rol/{rol}")
    public ResponseEntity<List<UsuarioResponse>> obtenerUsuariosPorRolPath(
            @PathVariable("rol") String rol) {
        List<UsuarioResponse> usuarios = usuarioService.buscarUsuariosPorRol(rol);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * GET /api/v1/usuarios/correo/{correo}
     * Obtiene usuario por correo
     */
    @GetMapping("correo/{correo}")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorCorreo(
            @PathVariable("correo") String correo) {
        UsuarioResponse usuario = usuarioService.obtenerUsuarioPorCorreo(correo);
        return ResponseEntity.ok(usuario);
    }




}
