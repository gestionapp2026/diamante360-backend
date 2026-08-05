package com.eldiamante360.producto.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class CombinacionTipoVentaInvalidaException extends ReglaNegocioException {

    public CombinacionTipoVentaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
