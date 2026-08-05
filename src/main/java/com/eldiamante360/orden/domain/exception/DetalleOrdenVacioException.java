package com.eldiamante360.orden.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class DetalleOrdenVacioException extends ReglaNegocioException {

    public DetalleOrdenVacioException() {
        super("La orden debe tener al menos un detalle");
    }
}
