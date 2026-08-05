package com.eldiamante360.inventario.presentation.dto.response;

import com.eldiamante360.inventario.domain.model.TipoMovimiento;

import java.math.BigDecimal;
import java.time.Instant;

public record MovimientoResponse(
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
