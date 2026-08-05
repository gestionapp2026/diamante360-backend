package com.eldiamante360.cliente.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarObservacionRequest(
        @NotBlank(message = "El texto de la observacion es obligatorio")
        @Size(max = 500, message = "El texto debe tener maximo 500 caracteres")
        String texto
) {
}
