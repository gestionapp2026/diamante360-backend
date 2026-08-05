package com.eldiamante360.producto.application.dto;

import java.math.BigDecimal;

public record ActualizarProductoCommand(
        Long productoId,
        String nombre,
        Long categoriaId,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        BigDecimal stockMinimo
) {
}
