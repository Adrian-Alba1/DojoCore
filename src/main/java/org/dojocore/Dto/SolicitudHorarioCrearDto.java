package org.dojocore.Dto;

import jakarta.validation.constraints.NotNull;

public record SolicitudHorarioCrearDto(
        @NotNull Integer idHorario,
        Integer idAlumno) {
}
