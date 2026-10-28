package org.dojocore.Dto;

import jakarta.validation.constraints.NotNull;

public record SolicitudCorreccionGradoCrearDto(
        @NotNull Integer idGradoSolicitado,
        Integer idAlumno) {
}
