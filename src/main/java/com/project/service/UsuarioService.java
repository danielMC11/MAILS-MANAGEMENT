package com.project.service;

import com.project.dto.UsuarioCrearRequest;
import com.project.dto.UsuarioResponse;

public interface UsuarioService {

    UsuarioResponse crearUsuario(UsuarioCrearRequest usuarioCrearRequest);


}
