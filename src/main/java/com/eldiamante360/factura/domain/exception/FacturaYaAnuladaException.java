package com.eldiamante360.factura.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class FacturaYaAnuladaException extends ReglaNegocioException {

    public FacturaYaAnuladaException(Long facturaId) {
        super("La factura %s ya se encuentra anulada".formatted(facturaId));
    }
}
