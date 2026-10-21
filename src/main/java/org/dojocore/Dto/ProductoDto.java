package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductoDto(
        Integer id,
        @NotBlank @Size(max = 150) String nombre,
        String descripcion,
        Boolean activo) {
}
