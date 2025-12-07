package com.project.service.impl;

import com.project.entity.Correo;
import com.project.repository.CorreoRepository;
import com.project.service.CorreoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CorreoServiceImpl implements CorreoService {


    @Autowired
    private CorreoRepository correoRepository;

    @Override
    public Correo registrarNuevoCorreo(Correo correo) {
        return correoRepository.save(correo);
    }
}
