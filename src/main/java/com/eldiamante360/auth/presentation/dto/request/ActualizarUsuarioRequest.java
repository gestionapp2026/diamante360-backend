package com.eldiamante360.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarUsuarioRequest(
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 120)
        String nombreCompleto,

        @NotNull(message = "El rol es obligatorio")
        Long rolId
) {
}
