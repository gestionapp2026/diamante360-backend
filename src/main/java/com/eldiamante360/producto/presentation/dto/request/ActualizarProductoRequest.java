package com.eldiamante360.producto.presentation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ActualizarProductoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre debe tener maximo 120 caracteres")
        String nombre,

        @NotNull(message = "La categoria es obligatoria")
        Long categoriaId,

        @NotNull(message = "El precio de compra es obligatorio")
        @DecimalMin(value = "0", message = "El precio de compra no puede ser negativo")
        BigDecimal precioCompra,

        @NotNull(message = "El precio de venta es obligatorio")
        @DecimalMin(value = "0", message = "El precio de venta no puede ser negativo")
        BigDecimal precioVenta,

        @NotNull(message = "El stock minimo es obligatorio")
        @DecimalMin(value = "0", message = "El stock minimo no puede ser negativo")
        BigDecimal stockMinimo
) {
}
