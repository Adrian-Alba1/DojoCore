package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoPedidoDto(
        @NotBlank String estado,
        @NotNull Integer idResponsable) {
}
