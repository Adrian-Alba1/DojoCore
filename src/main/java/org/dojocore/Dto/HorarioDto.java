package org.dojocore.Dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

public record HorarioDto(
        Integer id,
        @NotNull Integer idSede,
        String sede,
        @NotNull LocalTime horarioInicio,
        @NotNull LocalTime horarioFin,
        String descripcion,
        Boolean activo,
        @NotEmpty List<@NotNull Integer> idsDias,
        List<String>dias) {
}
