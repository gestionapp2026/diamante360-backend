package com.eldiamante360.factura.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class CantidadFacturadaInvalidaException extends ReglaNegocioException {

    public CantidadFacturadaInvalidaException() {
        super("La cantidad facturada debe ser mayor a cero");
    }
}
