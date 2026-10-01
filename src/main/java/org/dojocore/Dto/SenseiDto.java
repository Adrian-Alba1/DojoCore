package org.dojocore.Dto;

import jakarta.validation.constraints.NotNull;

public record SenseiDto(
        @NotNull Integer idPersona,
        String nombre
) {
}
