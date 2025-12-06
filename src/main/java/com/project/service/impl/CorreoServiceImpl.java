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
    public void registrarNuevoCorreo(Correo correo) {
        correoRepository.save(correo);
    }
}
