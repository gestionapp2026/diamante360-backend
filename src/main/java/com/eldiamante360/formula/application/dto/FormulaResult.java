package com.eldiamante360.formula.application.dto;

import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;

import java.math.BigDecimal;
import java.util.List;

public record FormulaResult(
        Long id,
        Long productoId,
        String productoNombre,
        BigDecimal cantidadBase,
        UnidadMedidaInsumo unidadBase,
        boolean activo,
        List<DetalleFormulaResult> detalles
) {
}
