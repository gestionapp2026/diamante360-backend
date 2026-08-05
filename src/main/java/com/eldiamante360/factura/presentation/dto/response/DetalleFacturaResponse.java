package com.eldiamante360.factura.presentation.dto.response;

import java.math.BigDecimal;

public record DetalleFacturaResponse(
        Long id,
        Long productoId,
        String productoNombre,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        BigDecimal porcentajeDescuento,
        BigDecimal subtotal,
        BigDecimal descuento,
        BigDecimal total
) {
}
