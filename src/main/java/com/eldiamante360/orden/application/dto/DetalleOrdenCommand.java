package com.eldiamante360.orden.application.dto;

import java.math.BigDecimal;

public record DetalleOrdenCommand(
        Long productoId,
        BigDecimal cantidad
) {
}
