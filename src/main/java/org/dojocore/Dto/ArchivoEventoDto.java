package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record ArchivoEventoDto(
        Integer id,
        Integer idEvento,
        @NotBlank @Size(max = 150) String nombre,
        @NotBlank @Size(max = 500) @Pattern(regexp = "^https?://\\S+$",
                message = "Debe Ser Un Enlace Que Empiece Con http:// o https://") String ruta,
        @Size(max = 50) String tipo,
        LocalDateTime fechaSubida) {
}
