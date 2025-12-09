package com.project.dto.entidad;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntidadResponse {

    private Long id;
    private String nombreEntidad;
    private String dominioCorreo;
}