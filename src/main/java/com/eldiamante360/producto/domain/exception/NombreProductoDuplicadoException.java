package com.eldiamante360.producto.domain.exception;

import com.eldiamante360.shared.domain.exception.RecursoDuplicadoException;

public class NombreProductoDuplicadoException extends RecursoDuplicadoException {

    public NombreProductoDuplicadoException(String nombre) {
        super("Ya existe un producto con el nombre '%s'".formatted(nombre));
    }
}
