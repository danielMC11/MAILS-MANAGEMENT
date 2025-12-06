package com.project.service.impl;

import com.project.entity.Cuenta;
import com.project.repository.CuentaRepository;
import com.project.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuentaServiceImpl implements CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Override
    public void guardarCuenta(Cuenta cuenta) {
        cuentaRepository.save(cuenta);
    }
}
