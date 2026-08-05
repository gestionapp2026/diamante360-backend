package com.eldiamante360.deudor.domain.model;

import com.eldiamante360.deudor.domain.exception.MedioPagoRequeridoException;
import com.eldiamante360.deudor.domain.exception.MontoAbonoInvalidoException;
import com.eldiamante360.shared.domain.model.MedioPago;

import java.math.BigDecimal;
import java.time.Instant;

public record Abono(
        Long id,
        Long cuentaPorCobrarId,
        BigDecimal monto,
        Long usuarioId,
        Instant fecha,
        MedioPago medioPago
) {

    public static Abono nuevo(Long cuentaPorCobrarId, BigDecimal monto, Long usuarioId, MedioPago medioPago) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MontoAbonoInvalidoException(monto);
        }
        if (medioPago == null) {
            throw new MedioPagoRequeridoException();
        }
        return new Abono(null, cuentaPorCobrarId, monto, usuarioId, Instant.now(), medioPago);
    }
}
