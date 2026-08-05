package com.eldiamante360.cliente.domain.exception;

import com.eldiamante360.shared.domain.exception.RecursoDuplicadoException;

public class NombreRutaDuplicadaException extends RecursoDuplicadoException {

    public NombreRutaDuplicadaException(String nombre) {
        super("Ya existe una ruta con el nombre '%s'".formatted(nombre));
    }
}
