package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtributoDto (
        Integer id,
        @NotBlank @Size(max = 50) String nombre){
}
