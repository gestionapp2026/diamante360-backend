package com.eldiamante360.deudor.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class MedioPagoRequeridoException extends ReglaNegocioException {

    public MedioPagoRequeridoException() {
        super("El medio de pago del abono es obligatorio");
    }
}
