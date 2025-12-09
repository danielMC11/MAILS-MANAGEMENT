package com.project.service;

import com.project.dto.UsuarioActualizarRequest;
import com.project.dto.UsuarioCrearRequest;
import com.project.dto.UsuarioResponse;
import com.project.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UsuarioService {

    UsuarioResponse crearUsuario(UsuarioCrearRequest usuarioCrearRequest);


    UsuarioResponse actualizarUsuario(Long id, UsuarioActualizarRequest usuarioActualizarRequest);

    void eliminarUsuario(Long id);

    //  MÉTODOS PARA LISTAR
    List<UsuarioResponse> listarTodosUsuarios();

    Page<UsuarioResponse> listarUsuariosPaginados(Pageable pageable);

    List<UsuarioResponse> buscarUsuariosPorNombre(String nombre);

    List<UsuarioResponse> buscarUsuariosPorRol(String rol);

    UsuarioResponse obtenerUsuarioPorId(Long id);

}
