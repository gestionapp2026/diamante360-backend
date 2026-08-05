package com.eldiamante360.insumoquimico.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record MovimientoInsumo(
        Long id,
        Long insumoId,
        Long loteId,
        TipoMovimientoInsumo tipoMovimiento,
        BigDecimal cantidad,
        BigDecimal stockResultante,
        String motivo,
        Long usuarioId,
        Instant fecha
) {

    public static MovimientoInsumo nuevo(Long insumoId, Long loteId, TipoMovimientoInsumo tipoMovimiento,
                                          BigDecimal cantidad, BigDecimal stockResultante, String motivo, Long usuarioId) {
        return new MovimientoInsumo(null, insumoId, loteId, tipoMovimiento, cantidad, stockResultante, motivo, usuarioId, Instant.now());
    }
}
