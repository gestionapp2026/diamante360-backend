package com.eldiamante360.deudor.application.dto;

import java.math.BigDecimal;

public record SaldoClienteResult(
        Long clienteId,
        BigDecimal saldoPendiente
) {
}
