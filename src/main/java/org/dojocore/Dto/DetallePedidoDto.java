package org.dojocore.Dto;

import java.math.BigDecimal;
import java.util.List;

public record DetallePedidoDto(
        Integer id,
        Integer idVariante,
        String producto,
        List<String> atributos,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal) {
}
