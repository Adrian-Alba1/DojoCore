package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;

public record GradoDto(
        Integer id,
        @NotBlank String nivel,
        @NotBlank String nombre,
        @NotBlank String color,
        @NotBlank String etapa,
        @NotBlank Integer orden){
}
