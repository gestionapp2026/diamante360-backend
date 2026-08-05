package com.eldiamante360.factura.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class DetalleFacturaVacioException extends ReglaNegocioException {

    public DetalleFacturaVacioException() {
        super("La factura debe tener al menos un detalle");
    }
}
