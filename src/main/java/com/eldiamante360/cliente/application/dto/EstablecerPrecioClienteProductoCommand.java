package com.eldiamante360.cliente.application.dto;

import java.math.BigDecimal;

public record EstablecerPrecioClienteProductoCommand(
        Long clienteId,
        Long productoId,
        BigDecimal precio
) {
}
