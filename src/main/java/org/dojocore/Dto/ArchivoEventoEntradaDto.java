package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ArchivoEventoEntradaDto(
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 50) String tipo,
        @Size(max = 500) @Pattern(regexp = "^https?://\\S+$",
        message = "Debe Ser Un Enlace Que Empiece Con http:// o https://") String ruta) {
}
