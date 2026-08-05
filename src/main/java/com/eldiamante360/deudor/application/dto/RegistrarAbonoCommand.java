package com.eldiamante360.deudor.application.dto;

import com.eldiamante360.shared.domain.model.MedioPago;

import java.math.BigDecimal;

public record RegistrarAbonoCommand(
        Long cuentaPorCobrarId,
        BigDecimal monto,
        Long usuarioId,
        MedioPago medioPago
) {
}
