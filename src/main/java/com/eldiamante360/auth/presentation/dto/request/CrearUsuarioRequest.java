package com.eldiamante360.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CrearUsuarioRequest(
        @NotBlank(message = "El username es obligatorio")
        @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El username solo puede contener letras, numeros, puntos, guiones y guion bajo")
        String username,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, max = 100, message = "La contrasena debe tener minimo 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contrasena debe contener al menos una letra y un numero")
        String password,

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 120)
        String nombreCompleto,

        @NotNull(message = "El rol es obligatorio")
        Long rolId
) {
}
