package org.dojocore.Dto;

import java.time.LocalDateTime;

public record MovimientoInventarioDto (
        Integer id,
        Integer idVariante,
        String tipo,
        Integer cantidad,
        LocalDateTime fecha,
        Integer idPersona,
        String motivo,
        Integer idPedido){
}
