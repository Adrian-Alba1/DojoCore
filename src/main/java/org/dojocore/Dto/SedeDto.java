package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SedeDto(
        Integer id,
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 200) String direccion,
        Boolean esCentral,
        Boolean esActivo) {
}
