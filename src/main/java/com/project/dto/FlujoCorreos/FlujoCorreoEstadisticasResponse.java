package com.project.dto.flujocorreo;

import com.project.enums.ETAPA;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlujoCorreoEstadisticasResponse {

    private Long totalFlujos;
    private Long flujosEnProgreso;
    private Long flujosCompletados;

    // Estadísticas por etapa
    private Map<ETAPA, Long> flujosPorEtapa;
    private Map<ETAPA, Double> tiempoPromedioPorEtapa; // Horas

    // Estadísticas por usuario
    private Map<String, Long> flujosPorUsuario; // nombreUsuario -> cantidad

    // Estadísticas por entidad
    private Map<String, Long> flujosPorEntidad; // nombreEntidad -> cantidad

    // Bottlenecks (cuellos de botella)
    private ETAPA etapaMasLenta;
    private Double tiempoMaximoEtapa; // Horas en la etapa más lenta
    private ETAPA etapaConMasPendientes;

    // Tiempos promedio
    private Double tiempoTotalPromedio; // Horas totales por flujo completo
    private Double tiempoRecepcionPromedio;
    private Double tiempoElaboracionPromedio;
    private Double tiempoRevisionPromedio;
    private Double tiempoAprobacionPromedio;
    private Double tiempoEnvioPromedio;

    // Flujos próximos a vencer (si hay plazos por etapa)
    private Long flujosPorVencer;

    // Últimos flujos completados
    private LocalDateTime ultimoFlujoCompletado;
}