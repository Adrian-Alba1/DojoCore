package org.dojocore.Dto;

import java.util.List;

public record MeDto(
        Integer idCuenta,
        Integer idPersona,
        String username,
        String nombre,
        List<String> roles) {
}
