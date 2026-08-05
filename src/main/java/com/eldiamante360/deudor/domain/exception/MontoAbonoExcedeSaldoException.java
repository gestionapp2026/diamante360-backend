package com.eldiamante360.deudor.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;

public class MontoAbonoExcedeSaldoException extends ReglaNegocioException {

    public MontoAbonoExcedeSaldoException(BigDecimal monto, BigDecimal saldoPendiente) {
        super("El abono de %s excede el saldo pendiente de %s".formatted(monto, saldoPendiente));
    }
}
