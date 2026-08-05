package com.eldiamante360.formula.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class CantidadProduccionInvalidaException extends ReglaNegocioException {

    public CantidadProduccionInvalidaException() {
        super("La cantidad a producir debe ser mayor a cero");
    }
}
