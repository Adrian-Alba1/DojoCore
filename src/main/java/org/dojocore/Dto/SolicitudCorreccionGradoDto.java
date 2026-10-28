package org.dojocore.Dto;

import java.time.LocalDateTime;

public record SolicitudCorreccionGradoDto(
        Integer id,
        Integer idAlumno,
        String nombreAlumno,
        Integer idGradoSolicitado,
        String gradoSolicitado,
        LocalDateTime fechaSolicitud,
        String estado) {
}
