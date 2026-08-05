package com.eldiamante360.inventario.application.dto;

import com.eldiamante360.inventario.domain.model.TipoMovimiento;

import java.math.BigDecimal;
import java.time.Instant;

public record MovimientoResult(
        Long id,
        Long productoId,
        TipoMovimiento tipoMovimiento,
        BigDecimal cantidad,
        BigDecimal stockResultante,
        String motivo,
        Long usuarioId,
        Instant fecha
) {
}
