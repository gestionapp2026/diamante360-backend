package com.eldiamante360.cliente.presentation.dto.request;

import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearClienteRequest(
        @NotNull(message = "El tipo de documento es obligatorio")
        TipoDocumentoCliente tipoDocumento,

        @NotBlank(message = "El numero de documento es obligatorio")
        @Size(max = 20, message = "El numero de documento debe tener maximo 20 caracteres")
        String numeroDocumento,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre debe tener maximo 150 caracteres")
        String nombre,

        @Size(max = 20, message = "El telefono debe tener maximo 20 caracteres")
        String telefono,

        @Email(message = "El email debe tener un formato valido")
        @Size(max = 120, message = "El email debe tener maximo 120 caracteres")
        String email,

        @Size(max = 200, message = "La direccion debe tener maximo 200 caracteres")
        String direccion,

        Long rutaId
) {
}
