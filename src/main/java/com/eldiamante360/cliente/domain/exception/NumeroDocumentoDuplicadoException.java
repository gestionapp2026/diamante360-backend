package com.eldiamante360.cliente.domain.exception;

import com.eldiamante360.shared.domain.exception.RecursoDuplicadoException;

public class NumeroDocumentoDuplicadoException extends RecursoDuplicadoException {

    public NumeroDocumentoDuplicadoException(String numeroDocumento) {
        super("Ya existe un cliente con el numero de documento '%s'".formatted(numeroDocumento));
    }
}
