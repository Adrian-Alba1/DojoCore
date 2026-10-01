package org.dojocore.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PersonaDto(
        Integer id,
        @NotBlank String nombre,
        @NotNull LocalDate fechaNacimiento,
        @NotBlank String telefono,
        @NotBlank @Email String email,
        String foto,
        Boolean activo,
        @NotNull Integer idGradoActual
        ) {
}
