package org.dojocore.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record PedidoItemsDto(
        @NotEmpty @Valid List<ItemPedidoDto> items) {
}
