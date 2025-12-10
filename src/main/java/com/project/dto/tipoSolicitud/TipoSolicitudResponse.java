// /dto/tipoSolicitud/TipoSolicitudResponse.java
package com.project.dto.tipoSolicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoSolicitudResponse {
    private Long id;
    private String nombre;
}