package com.eldiamante360.orden.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class ClienteInactivoException extends ReglaNegocioException {

    public ClienteInactivoException(Long clienteId) {
        super("No se puede crear una orden para el cliente %s porque esta inactivo".formatted(clienteId));
    }
}
