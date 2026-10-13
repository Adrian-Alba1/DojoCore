package org.dojocore.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ParticipacionEventoDto(
        Integer id,
        @NotNull Integer idEvento,
        @NotNull Integer idPersona,
        String nombrePersona,
        Integer idCategoria,
        String categoria,
        Integer idResultado,
        String resultado,
        Integer idGradoAnterior,
        Integer idGradoNuevo,
        @Size(max = 30) String mencion) {
}