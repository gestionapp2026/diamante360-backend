package com.eldiamante360.formula.application.dto;

import java.math.BigDecimal;

public record ProducirFormulaCommand(
        Long formulaId,
        BigDecimal cantidad,
        String motivo,
        Long usuarioId
) {
}
