package com.eldiamante360.deudor.presentation.dto.request;

import com.eldiamante360.shared.domain.model.MedioPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegistrarAbonoRequest(
        @NotNull
        @DecimalMin(value = "0", inclusive = false)
        BigDecimal monto,

        @NotNull(message = "El medio de pago es obligatorio")
        MedioPago medioPago
) {
}
