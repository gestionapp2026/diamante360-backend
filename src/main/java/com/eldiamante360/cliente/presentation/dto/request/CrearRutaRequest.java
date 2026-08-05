package com.eldiamante360.cliente.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearRutaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre debe tener maximo 80 caracteres")
        String nombre,

        @Size(max = 200, message = "La descripcion debe tener maximo 200 caracteres")
        String descripcion
) {
}
