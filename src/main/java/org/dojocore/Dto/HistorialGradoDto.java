package org.dojocore.Dto;

import java.time.LocalDateTime;

public record HistorialGradoDto(
        Integer id,
        Integer idPersona,
        Integer idGradoAnterior,
        String gradoAnterior,
        Integer idGradoNuevo,
        String gradoNuevo,
        Integer idExamen,
        String examen,
        LocalDateTime fecha) {
}
