package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.MovimientoInsumoResult;
import com.eldiamante360.insumoquimico.domain.model.MovimientoInsumo;

final class MovimientoInsumoAssembler {

    private MovimientoInsumoAssembler() {
    }

    static MovimientoInsumoResult toResult(MovimientoInsumo movimiento) {
        return new MovimientoInsumoResult(
                movimiento.id(),
                movimiento.insumoId(),
                movimiento.loteId(),
                movimiento.tipoMovimiento(),
                movimiento.cantidad(),
                movimiento.stockResultante(),
                movimiento.motivo(),
                movimiento.usuarioId(),
                movimiento.fecha());
    }
}
