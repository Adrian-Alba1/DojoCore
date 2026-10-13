package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaTorneoDto(
        Integer id,
        Integer idEvento,
        @NotBlank @Size(max = 30)String tipo) {
}
