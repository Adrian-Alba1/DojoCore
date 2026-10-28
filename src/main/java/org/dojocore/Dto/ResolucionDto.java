package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;

public record ResolucionDto(
        @NotBlank String estado) {
}
