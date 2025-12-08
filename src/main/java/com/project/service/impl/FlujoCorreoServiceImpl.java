package com.project.service.impl;

import com.project.entity.Correo;
import com.project.entity.FlujoCorreos;
import com.project.entity.Usuario;
import com.project.enums.ETAPA;
import com.project.repository.CorreoRepository;
import com.project.repository.FlujoCorreosRepository;
import com.project.repository.UsuarioRepository;
import com.project.service.FlujoCorreoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FlujoCorreoServiceImpl implements FlujoCorreoService {

    @Autowired
    private FlujoCorreosRepository flujoCorreosRepository;

    @Autowired
    private CorreoRepository correoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public FlujoCorreos iniciarFlujo(String correoId, String correoResponsable, ETAPA etapa, LocalDateTime fechaAsignacion) {

        Correo correo = correoRepository.findById(correoId)
                .orElseThrow(() -> new RuntimeException("ID correo no encontrado"));

        Usuario usuario = usuarioRepository.findByCorreo(correoResponsable)
                .orElseThrow(() -> new RuntimeException("Correo no encontrado"));


        FlujoCorreos flujoCorreo = new FlujoCorreos();
        flujoCorreo.setCorreo(correo);
        flujoCorreo.setUsuario(usuario);
        flujoCorreo.setEtapa(etapa);
        flujoCorreo.setFechaAsignacion(fechaAsignacion);
        return flujoCorreosRepository.save(flujoCorreo);

    }

    @Override
    public FlujoCorreos terminarFlujo(Long flujoId, LocalDateTime fechaFinalizacion) {

        FlujoCorreos flujoCorreo = flujoCorreosRepository.findById(flujoId)
                .orElseThrow(() -> new RuntimeException("ID flujo no encontrado"));

        flujoCorreo.setFechaFinalizacion(fechaFinalizacion);

        return flujoCorreosRepository.save(flujoCorreo);

    }

}
