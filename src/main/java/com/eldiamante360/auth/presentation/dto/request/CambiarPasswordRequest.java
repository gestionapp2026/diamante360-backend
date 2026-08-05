package com.eldiamante360.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CambiarPasswordRequest(
        @NotBlank(message = "La contrasena actual es obligatoria")
        String passwordActual,

        @NotBlank(message = "La nueva contrasena es obligatoria")
        @Size(min = 8, max = 100, message = "La contrasena debe tener minimo 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contrasena debe contener al menos una letra y un numero")
        String passwordNueva
) {
}
