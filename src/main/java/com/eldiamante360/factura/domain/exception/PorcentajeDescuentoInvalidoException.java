package com.eldiamante360.factura.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class PorcentajeDescuentoInvalidoException extends ReglaNegocioException {

    public PorcentajeDescuentoInvalidoException() {
        super("El porcentaje de descuento debe estar entre 0 y 100");
    }
}
