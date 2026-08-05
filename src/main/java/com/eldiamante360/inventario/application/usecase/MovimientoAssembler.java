package com.eldiamante360.inventario.application.usecase;

import com.eldiamante360.inventario.application.dto.MovimientoResult;
import com.eldiamante360.inventario.domain.model.MovimientoInventario;

final class MovimientoAssembler {

    private MovimientoAssembler() {
    }

    static MovimientoResult toResult(MovimientoInventario movimiento) {
        return new MovimientoResult(
                movimiento.id(),
                movimiento.productoId(),
                movimiento.tipoMovimiento(),
                movimiento.cantidad(),
                movimiento.stockResultante(),
                movimiento.motivo(),
                movimiento.usuarioId(),
                movimiento.fecha());
    }
}
