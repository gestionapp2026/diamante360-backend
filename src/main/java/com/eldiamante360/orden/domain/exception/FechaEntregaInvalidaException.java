package com.eldiamante360.orden.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class FechaEntregaInvalidaException extends ReglaNegocioException {

    public FechaEntregaInvalidaException() {
        super("La fecha de entrega es obligatoria y no puede ser anterior a hoy");
    }
}
