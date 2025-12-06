package com.project.service.impl;

import com.project.entity.Entidad;
import com.project.repository.EntidadRepository;
import com.project.service.EntidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntidadServiceImpl implements EntidadService {

    @Autowired
    private EntidadRepository entidadRepository;

    @Override
    public void crearEntidad(Entidad entidad) {
        entidadRepository.save(entidad);
    }
}
