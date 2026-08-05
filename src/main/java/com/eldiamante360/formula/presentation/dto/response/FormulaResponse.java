package com.eldiamante360.formula.presentation.dto.response;

import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;

import java.math.BigDecimal;
import java.util.List;

public record FormulaResponse(
        Long id,
        Long productoId,
        String productoNombre,
        BigDecimal cantidadBase,
        UnidadMedidaInsumo unidadBase,
        boolean activo,
        List<DetalleFormulaResponse> detalles
) {
}
