package com.eldiamante360.inventario.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record MovimientoInventario(
        Long id,
        Long productoId,
        TipoMovimiento tipoMovimiento,
        BigDecimal cantidad,
        BigDecimal stockResultante,
        String motivo,
        Long usuarioId,
        Instant fecha
) {

    public static MovimientoInventario nuevo(Long productoId, TipoMovimiento tipoMovimiento, BigDecimal cantidad,
                                               BigDecimal stockResultante, String motivo, Long usuarioId) {
        return new MovimientoInventario(null, productoId, tipoMovimiento, cantidad, stockResultante, motivo, usuarioId, Instant.now());
    }
}
