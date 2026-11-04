package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactoDto(
        Integer id,
        @NotBlank @Size(max = 150) String nombre,
        @NotBlank @Size(max = 20) @Pattern(regexp = "^[0-9+()\\- ]{7,20}$",
        message = "Debe Contener Solo Dígitos, espacios, +, - o parentesis (7 a 20 Caracteres)") String telefono,
        @NotBlank @Size(max = 50) String parentesco,
        Boolean esTutor) {
}
