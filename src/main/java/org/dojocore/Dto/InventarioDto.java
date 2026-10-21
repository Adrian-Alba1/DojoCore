package org.dojocore.Dto;

import java.util.List;

public record InventarioDto(
        Integer idVariante,
        Integer idProducto,
        String producto,
        List<String> atributos,
        Integer stockActual,
        Boolean activo) {
}
