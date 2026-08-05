package com.eldiamante360.formula.presentation.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProduccionFormulaResponse(
        Long formulaId,
        Long productoId,
        String productoNombre,
        BigDecimal cantidadProducida,
        List<ConsumoInsumoResponse> consumos
) {
}
