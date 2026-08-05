package com.eldiamante360.deudor.application.dto;

import com.eldiamante360.shared.domain.model.MedioPago;

import java.math.BigDecimal;
import java.time.Instant;

public record AbonoResult(
        Long id,
        Long cuentaPorCobrarId,
        BigDecimal monto,
        Long usuarioId,
        Instant fecha,
        MedioPago medioPago
) {
}
