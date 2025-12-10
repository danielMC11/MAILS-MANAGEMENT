// /dto/tipoSolicitud/TipoSolicitudSimpleResponse.java
// Para cuando solo necesitas mostrar información básica
package com.project.dto.tipoSolicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoSolicitudSimpleResponse {
    private Long id;
    private String nombre;
}