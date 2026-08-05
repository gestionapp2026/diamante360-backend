package com.eldiamante360.producto.application.dto;

public record CategoriaResult(
        Long id,
        String nombre,
        String descripcion,
        boolean activo
) {
}
