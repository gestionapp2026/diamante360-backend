package com.eldiamante360.factura.application.dto;

import java.math.BigDecimal;

public record DetalleFacturaCommand(
        Long productoId,
        BigDecimal cantidad,
        BigDecimal porcentajeDescuento
) {
}
