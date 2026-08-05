package com.eldiamante360.orden.application.dto;

import java.math.BigDecimal;

public record DetalleOrdenResult(
        Long id,
        Long productoId,
        String productoNombre,
        BigDecimal cantidad
) {
}
