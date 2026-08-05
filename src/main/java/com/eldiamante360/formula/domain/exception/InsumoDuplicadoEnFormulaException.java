package com.eldiamante360.formula.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class InsumoDuplicadoEnFormulaException extends ReglaNegocioException {

    public InsumoDuplicadoEnFormulaException(Long insumoId) {
        super("El insumo quimico %d esta repetido en la formula".formatted(insumoId));
    }
}
