package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ValorAtributoDto (
        Integer id,
        Integer idAtributo,
        String atributo,
        @NotBlank @Size(max = 100)String valor){
}
