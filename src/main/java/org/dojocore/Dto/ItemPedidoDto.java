package org.dojocore.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemPedidoDto(
        @NotNull Integer idVariante,
        @NotNull @Positive Integer cantidad) {
}
