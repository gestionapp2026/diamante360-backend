package com.eldiamante360.factura.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class ClienteInactivoException extends ReglaNegocioException {

    public ClienteInactivoException(Long clienteId) {
        super("No se puede facturar al cliente %s porque esta inactivo".formatted(clienteId));
    }
}
