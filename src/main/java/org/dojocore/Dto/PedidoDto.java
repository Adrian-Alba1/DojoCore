package org.dojocore.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoDto(
        Integer id,
        Integer idPersona,
        LocalDateTime fecha,
        String estado,
        List<DetallePedidoDto> detalles,
        BigDecimal total) {
}
