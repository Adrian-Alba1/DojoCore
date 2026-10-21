package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MovimientoManualDto (
        @NotBlank @Size(max = 30) String tipo,
        @NotNull @Positive Integer cantidad,
        @NotNull Integer idPersona,
        @Size(max = 225) String motivo){
}
