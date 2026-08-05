package com.eldiamante360.insumoquimico.presentation.dto.response;

import com.eldiamante360.insumoquimico.domain.model.TipoMovimientoInsumo;

import java.math.BigDecimal;
import java.time.Instant;

public record MovimientoInsumoResponse(
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
