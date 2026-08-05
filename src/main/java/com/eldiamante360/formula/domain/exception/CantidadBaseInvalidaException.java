package com.eldiamante360.formula.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class CantidadBaseInvalidaException extends ReglaNegocioException {

    public CantidadBaseInvalidaException() {
        super("La cantidad base de la formula debe ser mayor a cero");
    }
}
