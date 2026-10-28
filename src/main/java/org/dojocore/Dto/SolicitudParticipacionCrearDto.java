package org.dojocore.Dto;

import jakarta.validation.constraints.NotNull;

public record SolicitudParticipacionCrearDto(
        @NotNull Integer idEvento,
        Integer idCategoria,
        Integer idPersona) {
}
