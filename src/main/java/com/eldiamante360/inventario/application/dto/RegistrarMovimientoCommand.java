package com.eldiamante360.inventario.application.dto;

import com.eldiamante360.inventario.domain.model.TipoMovimiento;

import java.math.BigDecimal;

public record RegistrarMovimientoCommand(
        Long productoId,
        TipoMovimiento tipoMovimiento,
        BigDecimal cantidad,
        String motivo,
        Long usuarioId
) {
}
