package com.eldiamante360.factura.presentation.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DetalleFacturaRequest(
        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0", inclusive = false, message = "La cantidad debe ser mayor a cero")
        BigDecimal cantidad,

        @NotNull(message = "El porcentaje de descuento es obligatorio")
        @DecimalMin(value = "0", message = "El porcentaje de descuento no puede ser negativo")
        @DecimalMax(value = "100", message = "El porcentaje de descuento no puede ser mayor a 100")
        BigDecimal porcentajeDescuento
) {
}
