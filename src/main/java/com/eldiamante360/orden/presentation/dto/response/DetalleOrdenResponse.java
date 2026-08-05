package com.eldiamante360.orden.presentation.dto.response;

import java.math.BigDecimal;

public record DetalleOrdenResponse(
        Long id,
        Long productoId,
        String productoNombre,
        BigDecimal cantidad
) {
}
