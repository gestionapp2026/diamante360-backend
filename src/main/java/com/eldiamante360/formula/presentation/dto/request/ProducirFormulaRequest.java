package com.eldiamante360.formula.presentation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProducirFormulaRequest(
        @NotNull(message = "La cantidad a producir es obligatoria")
        @DecimalMin(value = "0", inclusive = false, message = "La cantidad a producir debe ser mayor a cero")
        BigDecimal cantidad,

        @Size(max = 200, message = "El motivo no puede superar los 200 caracteres")
        String motivo
) {
}
