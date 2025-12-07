package com.project.service;

import com.project.dto.UsuarioActualizarRequest;
import com.project.dto.UsuarioCrearRequest;
import com.project.dto.UsuarioResponse;
import com.project.entity.Usuario;

public interface UsuarioService {

    UsuarioResponse crearUsuario(UsuarioCrearRequest usuarioCrearRequest);


    UsuarioResponse actualizarUsuario(Long id, UsuarioActualizarRequest usuarioActualizarRequest);

    void eliminarUsuario(Long id);

}
