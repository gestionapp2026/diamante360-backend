package com.eldiamante360.deudor.presentation.dto.response;

import java.math.BigDecimal;

public record SaldoClienteResponse(
        Long clienteId,
        BigDecimal saldoPendiente
) {
}
