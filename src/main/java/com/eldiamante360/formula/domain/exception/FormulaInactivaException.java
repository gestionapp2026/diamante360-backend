package com.eldiamante360.formula.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class FormulaInactivaException extends ReglaNegocioException {

    public FormulaInactivaException(Long formulaId) {
        super("La formula %d esta inactiva, no se puede usar para producir".formatted(formulaId));
    }
}
