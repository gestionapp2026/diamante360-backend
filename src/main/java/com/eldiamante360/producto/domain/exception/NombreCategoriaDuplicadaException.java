package com.eldiamante360.producto.domain.exception;

import com.eldiamante360.shared.domain.exception.RecursoDuplicadoException;

public class NombreCategoriaDuplicadaException extends RecursoDuplicadoException {

    public NombreCategoriaDuplicadaException(String nombre) {
        super("Ya existe una categoria con el nombre '%s'".formatted(nombre));
    }
}
