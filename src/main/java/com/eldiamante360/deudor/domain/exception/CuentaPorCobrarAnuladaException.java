package com.eldiamante360.deudor.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class CuentaPorCobrarAnuladaException extends ReglaNegocioException {

    public CuentaPorCobrarAnuladaException(Long cuentaPorCobrarId) {
        super("La cuenta por cobrar %s se encuentra anulada".formatted(cuentaPorCobrarId));
    }
}
