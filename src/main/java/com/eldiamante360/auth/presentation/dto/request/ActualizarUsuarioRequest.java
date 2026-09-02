package com.eldiamante360.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarUsuarioRequest(
        @NotBlank(message = "El username es obligatorio")
        @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El username solo puede contener letras, numeros, puntos, guiones y guion bajo")
        String username,

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 120)
        String nombreCompleto,

        @NotNull(message = "El rol es obligatorio")
        Long rolId
) {
}
