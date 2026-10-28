package org.dojocore.Dto;

import java.time.LocalDateTime;

public record SolicitudHorarioDto(
        Integer id,
        Integer idAlumno,
        String nombreAlumno,
        Integer idHorario,
        LocalDateTime fechaSolicitud,
        String estado) {
}
