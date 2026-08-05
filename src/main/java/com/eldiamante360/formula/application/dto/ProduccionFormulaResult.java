package com.eldiamante360.formula.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProduccionFormulaResult(
        Long formulaId,
        Long productoId,
        String productoNombre,
        BigDecimal cantidadProducida,
        List<ConsumoInsumoResult> consumos
) {
}
