package com.eldiamante360.insumoquimico.application.dto;

import com.eldiamante360.insumoquimico.domain.model.TipoMovimientoInsumo;

import java.math.BigDecimal;
import java.time.Instant;

public record MovimientoInsumoResult(
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
}
