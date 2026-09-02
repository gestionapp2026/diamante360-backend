package com.eldiamante360.cliente.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ActualizarClienteRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre debe tener maximo 150 caracteres")
        String nombre,

        @Size(max = 5, message = "Maximo 5 telefonos por cliente")
        List<@Size(max = 20, message = "Cada telefono debe tener maximo 20 caracteres") String> telefonos,

        @Email(message = "El email debe tener un formato valido")
        @Size(max = 120, message = "El email debe tener maximo 120 caracteres")
        String email,

        @Size(max = 200, message = "La direccion debe tener maximo 200 caracteres")
        String direccion
) {
}
