package com.eldiamante360.cliente.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequest(
        @NotNull(message = "El campo activo es obligatorio") Boolean activo
) {
}
