package com.eldiamante360.formula.application.dto;

import java.math.BigDecimal;

public record DetalleFormulaCommand(
        Integer numero,
        Long insumoId,
        BigDecimal cantidad
) {
}
