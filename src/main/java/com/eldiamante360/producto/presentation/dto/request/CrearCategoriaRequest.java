package com.eldiamante360.producto.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearCategoriaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 60, message = "El nombre debe tener maximo 60 caracteres")
        String nombre,

        @Size(max = 150, message = "La descripcion debe tener maximo 150 caracteres")
        String descripcion
) {
}
