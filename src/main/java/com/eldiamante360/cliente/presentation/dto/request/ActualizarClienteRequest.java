package com.eldiamante360.cliente.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarClienteRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre debe tener maximo 150 caracteres")
        String nombre,

        @Size(max = 20, message = "El telefono debe tener maximo 20 caracteres")
        String telefono,

        @Email(message = "El email debe tener un formato valido")
        @Size(max = 120, message = "El email debe tener maximo 120 caracteres")
        String email,

        @Size(max = 200, message = "La direccion debe tener maximo 200 caracteres")
        String direccion
) {
}
