package com.eldiamante360.orden.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class CantidadOrdenInvalidaException extends ReglaNegocioException {

    public CantidadOrdenInvalidaException() {
        super("La cantidad de la orden debe ser mayor a cero");
    }
}
