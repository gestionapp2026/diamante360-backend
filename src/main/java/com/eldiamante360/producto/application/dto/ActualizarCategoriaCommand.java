package com.eldiamante360.producto.application.dto;

public record ActualizarCategoriaCommand(
        Long categoriaId,
        String nombre,
        String descripcion
) {
}
