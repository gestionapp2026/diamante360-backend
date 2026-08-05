package com.eldiamante360.formula.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class DetalleFormulaVacioException extends ReglaNegocioException {

    public DetalleFormulaVacioException() {
        super("La formula debe tener al menos un detalle (insumo)");
    }
}
