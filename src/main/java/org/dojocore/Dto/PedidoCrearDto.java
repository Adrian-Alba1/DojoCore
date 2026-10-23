package org.dojocore.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PedidoCrearDto(
        @NotNull Integer idPersona,
        @NotEmpty @Valid List<ItemPedidoDto> items) {
}
