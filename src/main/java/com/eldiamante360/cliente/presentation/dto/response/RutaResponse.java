package com.eldiamante360.cliente.presentation.dto.response;

public record RutaResponse(
        Long id,
        String nombre,
        String descripcion,
        boolean activo
) {
}
