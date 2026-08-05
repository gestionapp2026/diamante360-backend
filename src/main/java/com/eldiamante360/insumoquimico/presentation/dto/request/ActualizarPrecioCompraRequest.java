package com.eldiamante360.insumoquimico.presentation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ActualizarPrecioCompraRequest(
        @NotNull(message = "El precio de compra es obligatorio")
        @DecimalMin(value = "0", message = "El precio de compra no puede ser negativo")
        BigDecimal precioCompra
) {
}
