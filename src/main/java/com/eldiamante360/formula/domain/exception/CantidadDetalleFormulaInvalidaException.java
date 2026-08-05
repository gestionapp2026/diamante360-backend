package com.eldiamante360.formula.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class CantidadDetalleFormulaInvalidaException extends ReglaNegocioException {

    public CantidadDetalleFormulaInvalidaException() {
        super("La cantidad de un detalle de formula debe ser mayor a cero");
    }
}
