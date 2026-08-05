package com.eldiamante360.cliente.presentation.dto.response;

import java.math.BigDecimal;

public record PrecioClienteProductoResponse(
        Long id,
        Long clienteId,
        Long productoId,
        String productoNombre,
        BigDecimal precio
) {
}
