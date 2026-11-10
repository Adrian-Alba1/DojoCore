package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record ArchivoEventoDto(
        Integer id,
        Integer idEvento,
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 50) String tipo,
        String contentType,
        LocalDateTime fechaSubida,
        boolean almacenado,
        boolean vistaPrevia,
        String enlaceExterno) {
}
