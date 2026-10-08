package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventoDto (

        Integer id,
        @NotNull Integer idTipoEvento,
        String tipo,
        @NotBlank @Size(max = 150) String nombre,
        @NotNull LocalDateTime fechaInicio,
        @NotNull LocalDateTime fechaFin,
        @NotBlank @Size(max = 200) String lugar,
        String descripcion,
        @PositiveOrZero BigDecimal costo,
        @NotBlank @Size(max = 20) String estado,
        @NotNull LocalDateTime fechaPublicacion,
        @NotBlank @Size(max = 20) String visibilidad,
        @NotNull Integer idSenseiResponsable,
        @Size(max = 100) String tipoCertificacion
) {
}
