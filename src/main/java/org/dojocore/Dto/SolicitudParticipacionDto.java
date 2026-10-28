package org.dojocore.Dto;

import java.time.LocalDateTime;

public record SolicitudParticipacionDto(
        Integer Id,
        Integer idEvento,
        String nombreEvento,
        Integer IdPersona,
        String nombrePersona,
        Integer idCategoria,
        LocalDateTime fechaSolicitud,
        String estado) {
}
