package org.dojocore.Dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AlumnoDto(
        @NotNull Integer idPersona,
        String nombre,
        @NotNull LocalDate fechaIngreso,
        Integer idSenseiPrincipal
        ) {
}
