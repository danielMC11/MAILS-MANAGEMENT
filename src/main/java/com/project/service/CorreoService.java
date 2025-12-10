package com.project.service;

import com.project.dto.correo.CorreoEstadisticasResponse;
import com.project.dto.correo.CorreoFilterRequest;
import com.project.dto.correo.CorreoResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import com.project.entity.Correo;
import java.time.LocalDateTime;


public interface CorreoService {

    Correo registrarNuevoCorreo(Correo correo);

    void registrarEnvioFinal(String correoId, LocalDateTime fechaRespuesta);

    void ingresarDatosEntrada(String correoId, String radicadoEntrada, Integer plazoRespuestaEnDias, String tipoSolicitudNombre, String nivelUrgencia);

    void ingresarRadicadoSalida(String correoId, String radicadoSalida);

    void ingresarGestionId(String correoId, String gestionId);

    void vencerCorreo(String correoId);

    // Consultas básicas
    CorreoResponse obtenerCorreoPorId(String id);
    Page<CorreoResponse> listarCorreos(CorreoFilterRequest filtro);
    List<CorreoResponse> buscarCorreosPorAsunto(String asunto);

    // Consultas por relaciones
    List<CorreoResponse> obtenerCorreosPorCuenta(Long cuentaId);
    List<CorreoResponse> obtenerCorreosPorEntidad(Long entidadId);
    List<CorreoResponse> obtenerCorreosPorTipoSolicitud(Long tipoSolicitudId);
    List<CorreoResponse> obtenerCorreosPorEstado(String estado);

    // Consultas especiales
    List<CorreoResponse> obtenerCorreosVencidos();
    List<CorreoResponse> obtenerCorreosPorVencer(Integer dias);
    List<CorreoResponse> obtenerCorreosSinRespuesta();
    List<CorreoResponse> obtenerCorreosConRespuestaReciente(Integer dias);

    // Estadísticas
    CorreoEstadisticasResponse obtenerEstadisticas();
    CorreoEstadisticasResponse obtenerEstadisticasPorPeriodo(
            LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Búsqueda por radicado
    CorreoResponse obtenerCorreoPorRadicadoEntrada(String radicado);
    CorreoResponse obtenerCorreoPorRadicadoSalida(String radicado);



}
