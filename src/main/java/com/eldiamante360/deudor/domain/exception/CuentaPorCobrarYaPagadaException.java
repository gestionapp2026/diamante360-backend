package com.eldiamante360.deudor.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class CuentaPorCobrarYaPagadaException extends ReglaNegocioException {

    public CuentaPorCobrarYaPagadaException(Long cuentaPorCobrarId) {
        super("La cuenta por cobrar %s ya se encuentra pagada en su totalidad".formatted(cuentaPorCobrarId));
    }
}
