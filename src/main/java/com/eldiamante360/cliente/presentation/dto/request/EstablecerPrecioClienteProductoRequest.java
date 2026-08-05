package com.eldiamante360.cliente.presentation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record EstablecerPrecioClienteProductoRequest(
        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0", message = "El precio no puede ser negativo")
        BigDecimal precio
) {
}
