package com.project.controller;

import com.project.dto.FiltroCorreoRequestDTO;
import com.project.dto.dashboard.DashboardEstadisticasDTO;
import com.project.dto.dashboard.DashboardEstadisticasResponse;
import com.project.dto.dashboard.MetricaResponse;
import com.project.entity.Correo;
import com.project.entity.FlujoCorreos;
import com.project.entity.TipoSolicitud;
import com.project.enums.ESTADO;
import com.project.enums.URGENCIA;
import com.project.repository.FlujoCorreoRepository;
import com.project.service.DashboardService;
import com.project.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private FlujoCorreoRepository flujoCorreoRepository;



    @GetMapping("/estadisticas")
    public ResponseEntity<DashboardEstadisticasDTO> obtenerEstadisticasCompletas() {
        return ResponseEntity.ok(dashboardService.obtenerEstadisticasCompletas());
    }

    @GetMapping("/estadisticas/correos")
    public ResponseEntity<DashboardEstadisticasResponse> obtenerEstadisticasCorreos() {
        return ResponseEntity.ok(dashboardService.obtenerEstadisticasDashboard());
    }

    @GetMapping("/distribucion/estado")
    public ResponseEntity<Map<String, Long>> obtenerDistribucionPorEstado() {
        return ResponseEntity.ok(dashboardService.obtenerDistribucionPorEstado());
    }

    @GetMapping("/distribucion/etapa")
    public ResponseEntity<Map<String, Long>> obtenerDistribucionPorEtapa() {
        return ResponseEntity.ok(dashboardService.obtenerDistribucionPorEtapa());
    }

    @GetMapping("/indicadores")
    public ResponseEntity<List<MetricaResponse>> obtenerKPIsPrincipales() {
        return ResponseEntity.ok(dashboardService.obtenerKPIsPrincipales());
    }


    @GetMapping("/opciones-filtro")
    public ResponseEntity<Map<String, Object>> cargarOpcionesDashboard() {

        Map<String, Object> opciones = new HashMap<>();

        List<Map<String, Object>> gestores = dashboardService.obtenerGestores();
        opciones.put("gestores", gestores);

        // 2. ENTIDADES (Todas las entidades disponibles)
        List<Map<String, Object>> entidades = dashboardService.obtenerEntidades();
        opciones.put("entidades", entidades);

        // 3. ESTADOS (Todos los estados posibles)
        List<Map<String, String>> estados = Arrays.stream(ESTADO.values())
                .map(estado -> {
                    Map<String, String> estadoMap = new HashMap<>();
                    estadoMap.put("id", estado.name());
                    estadoMap.put("nombre", estado.name());
                    return estadoMap;
                })
                .collect(Collectors.toList());
        opciones.put("estados", estados);

        // 4. TIPOS DE SOLICITUD
        List<Map<String, Object>> tiposSolicitud = dashboardService.obtenerTipoSolicitudes();
        opciones.put("tiposSolicitud", tiposSolicitud);

        // 5. URGENCIAS
        List<Map<String, String>> urgencias = Arrays.stream(URGENCIA.values())
                .map(urgencia -> {
                    Map<String, String> urgenciaMap = new HashMap<>();
                    urgenciaMap.put("id", urgencia.name());
                    urgenciaMap.put("nombre", urgencia.name());
                    return urgenciaMap;
                })
                .collect(Collectors.toList());
        opciones.put("urgencias", urgencias);

        // 6. Campos de búsqueda (sugerencias para el placeholder)
        List<String> camposBusqueda = Arrays.asList(
                "Radicado de entrada",
                "Radicado de salida",
                "Asunto",
                "Correo remitente",
                "ID de gestión"
        );
        opciones.put("camposBusqueda", camposBusqueda);

        return ResponseEntity.ok(opciones);
    }

    @PostMapping("/filtrar")
    public ResponseEntity<Map<String, Object>> aplicarFiltros(
            @RequestBody FiltroCorreoRequestDTO filtro,
            @RequestParam(defaultValue = "0", name = "pagina") int pagina,
            @RequestParam(defaultValue = "20", name = "tamano") int tamano,
            @RequestParam(defaultValue = "fechaRecepcion", name = "ordenarPor") String ordenarPor,
            @RequestParam(defaultValue = "DESC", name = "direccion") String direccion) {

        try {
            // 1. Validar fechas
            if (filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {
                if (filtro.getFechaInicio().isAfter(filtro.getFechaFin())) {
                    return ResponseEntity.badRequest()
                            .body(Map.of(
                                    "error", "La fecha de inicio no puede ser posterior a la fecha de fin",
                                    "codigo", "FECHA_INVALIDA"
                            ));
                }
            }

            // 2. Preparar paginación
            Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direccion)
                    ? Sort.Direction.ASC : Sort.Direction.DESC;

            Pageable pageable = PageRequest.of(
                    pagina,
                    tamano,
                    Sort.by(sortDirection, ordenarPor)
            );

            // 3. Llamar al servicio para obtener correos filtrados
            Page<Correo> paginaCorreos = dashboardService.filtrarCorreos(filtro, pageable);


            // 5. Convertir correos a DTO para evitar problemas de serialización
            List<Map<String, Object>> correosDTO = convertirCorreosADTO(paginaCorreos.getContent());

            // 6. Construir respuesta
            Map<String, Object> respuesta = new HashMap<>();

            // Datos principales
            respuesta.put("correos", correosDTO);

            // Información de paginación
            respuesta.put("paginaActual", paginaCorreos.getNumber());
            respuesta.put("totalPaginas", paginaCorreos.getTotalPages());
            respuesta.put("totalElementos", paginaCorreos.getTotalElements());
            respuesta.put("tamanoPagina", paginaCorreos.getSize());
            respuesta.put("esPrimeraPagina", paginaCorreos.isFirst());
            respuesta.put("esUltimaPagina", paginaCorreos.isLast());
            respuesta.put("totalElementosPagina", paginaCorreos.getNumberOfElements());



            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Parámetros de filtro inválidos",
                            "detalle", e.getMessage(),
                            "codigo", "PARAMETRO_INVALIDO"
                    ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Error interno del servidor",
                            "mensaje", "No se pudieron aplicar los filtros",
                            "codigo", "ERROR_INTERNO"
                    ));
        }
    }

    // Método auxiliar para convertir Correos a DTOs
    private List<Map<String, Object>> convertirCorreosADTO(List<Correo> correos) {
        return correos.stream()
                .map(this::convertirCorreoADTO)
                .collect(Collectors.toList());
    }

    private Map<String, Object> convertirCorreoADTO(Correo correo) {
        Map<String, Object> dto = new HashMap<>();

        // Información básica del correo
        dto.put("id", correo.getId());
        dto.put("asunto", correo.getAsunto());
        dto.put("estado", correo.getEstado() != null ? correo.getEstado().name() : null);
        dto.put("urgencia", correo.getUrgencia() != null ? correo.getUrgencia().name() : null);
        dto.put("fechaRecepcion", correo.getFechaRecepcion());
        dto.put("fechaRespuesta", correo.getFechaRespuesta());
        dto.put("radicadoEntrada", correo.getRadicadoEntrada());
        dto.put("radicadoSalida", correo.getRadicadoSalida());
        dto.put("plazoRespuestaEnDias", correo.getPlazoRespuestaEnDias());

        // Información de la cuenta (remitente)
        if (correo.getCuenta() != null) {
            Map<String, Object> cuentaInfo = new HashMap<>();
            cuentaInfo.put("id", correo.getCuenta().getId());
            cuentaInfo.put("nombre", correo.getCuenta().getNombreCuenta());
            cuentaInfo.put("correo", correo.getCuenta().getCorreoCuenta());

            if (correo.getCuenta().getEntidad() != null) {
                cuentaInfo.put("entidadId", correo.getCuenta().getEntidad().getId());
                cuentaInfo.put("entidadNombre", correo.getCuenta().getEntidad().getNombreEntidad());
            }

            dto.put("cuenta", cuentaInfo);
        }

        // Información del tipo de solicitud
        if (correo.getTipoSolicitud() != null) {
            Map<String, Object> tipoInfo = new HashMap<>();
            tipoInfo.put("id", correo.getTipoSolicitud().getId());
            tipoInfo.put("nombre", correo.getTipoSolicitud().getNombre());
            dto.put("tipoSolicitud", tipoInfo);
        }

        // Información de asignación (si está asignado a un gestor)
        List<FlujoCorreos> todosFlujos = flujoCorreoRepository
                .findByCorreoOrderByFechaAsignacionDesc(correo);

        // Crear lista de flujos con la información solicitada
        List<Map<String, Object>> listaFlujos = new ArrayList<>();

        for (FlujoCorreos flujo : todosFlujos) {
            Map<String, Object> flujoInfo = new HashMap<>();

            // Información del usuario (gestor)
            if (flujo.getUsuario() != null) {
                flujoInfo.put("nombreUsuario",
                        flujo.getUsuario().getNombres() + " " + flujo.getUsuario().getApellidos());
                flujoInfo.put("correoUsuario", flujo.getUsuario().getCorreo());
                flujoInfo.put("idUsuario", flujo.getUsuario().getId());
            }

            // Fechas
            flujoInfo.put("fechaAsignacion", flujo.getFechaAsignacion());
            flujoInfo.put("fechaFinalizacion", flujo.getFechaFinalizacion());

            // Etapa
            flujoInfo.put("etapa", flujo.getEtapa() != null ? flujo.getEtapa().name() : null);

            // Estado del flujo
            flujoInfo.put("estaActivo", flujo.getFechaFinalizacion() == null);

            // ID del flujo
            flujoInfo.put("idFlujo", flujo.getId());

            listaFlujos.add(flujoInfo);
        }

        // Agregar la lista de flujos al DTO
        dto.put("flujos", listaFlujos);


        // Calcular si está atrasado
        if (correo.getPlazoRespuestaEnDias() != null && correo.getFechaRecepcion() != null) {
            LocalDateTime fechaLimite = correo.getFechaRecepcion()
                    .plusDays(correo.getPlazoRespuestaEnDias());
            dto.put("fechaLimite", fechaLimite);
            dto.put("estaAtrasado",
                    LocalDateTime.now().isAfter(fechaLimite)
            );
        }

        return dto;
    }

    // Endpoint para limpiar filtros (opcional pero útil)
    @PostMapping("/limpiar-filtros")
    public ResponseEntity<Map<String, Object>> limpiarFiltros(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamano) {

        FiltroCorreoRequestDTO filtroVacio = new FiltroCorreoRequestDTO();
        Pageable pageable = PageRequest.of(pagina, tamano,
                Sort.by(Sort.Direction.DESC, "fechaRecepcion"));

        Page<Correo> paginaCorreos = dashboardService.filtrarCorreos(filtroVacio, pageable);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("correos", convertirCorreosADTO(paginaCorreos.getContent()));
        respuesta.put("paginaActual", paginaCorreos.getNumber());
        respuesta.put("totalPaginas", paginaCorreos.getTotalPages());
        respuesta.put("totalElementos", paginaCorreos.getTotalElements());
        respuesta.put("mensaje", "Filtros limpiados correctamente");
        respuesta.put("filtrosAplicados", Map.of(
                "descripcion", "Sin filtros",
                "tieneFiltros", false
        ));

        return ResponseEntity.ok(respuesta);
    }




}