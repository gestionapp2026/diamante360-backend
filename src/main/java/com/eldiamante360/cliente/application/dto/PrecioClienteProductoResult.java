package com.eldiamante360.cliente.application.dto;

import java.math.BigDecimal;

public record PrecioClienteProductoResult(
        Long id,
        Long clienteId,
        Long productoId,
        String productoNombre,
        BigDecimal precio
) {
}
