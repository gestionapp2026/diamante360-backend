package com.eldiamante360.formula.presentation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DetalleFormulaRequest(
        @NotNull(message = "El numero de frasco es obligatorio")
        Integer numero,

        @NotNull(message = "El insumo quimico es obligatorio")
        Long insumoId,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0", inclusive = false, message = "La cantidad debe ser mayor a cero")
        BigDecimal cantidad
) {
}
