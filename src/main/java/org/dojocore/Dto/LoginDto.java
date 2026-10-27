package org.dojocore.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDto(
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Size(max = 100) String password) {
}
