package com.eldiamante360.orden.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class OrdenYaDespachadaException extends ReglaNegocioException {

    public OrdenYaDespachadaException(Long ordenId) {
        super("La orden %s ya se encuentra despachada".formatted(ordenId));
    }
}
