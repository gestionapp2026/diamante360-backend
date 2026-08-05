package com.eldiamante360.orden.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class OrdenYaAnuladaException extends ReglaNegocioException {

    public OrdenYaAnuladaException(Long ordenId) {
        super("La orden %s ya se encuentra anulada".formatted(ordenId));
    }
}
