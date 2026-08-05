package com.eldiamante360.deudor.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;

public class MontoAbonoInvalidoException extends ReglaNegocioException {

    public MontoAbonoInvalidoException(BigDecimal monto) {
        super("El monto del abono debe ser mayor a cero: %s".formatted(monto));
    }
}
