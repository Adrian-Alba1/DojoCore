package org.dojocore.Dto;

import java.util.List;

public record TokenDto(
        String token,
        String tipo,
        long expiraEnSegundos,
        String username,
        Integer idPersona,
        List<String> roles) {
}
