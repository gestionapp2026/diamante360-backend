package com.eldiamante360.producto.presentation.dto.response;

public record CategoriaResponse(
        Long id,
        String nombre,
        String descripcion,
        boolean activo
) {
}
