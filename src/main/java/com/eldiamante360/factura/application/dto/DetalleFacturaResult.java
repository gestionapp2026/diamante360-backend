package com.eldiamante360.factura.application.dto;

import java.math.BigDecimal;

public record DetalleFacturaResult(
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
