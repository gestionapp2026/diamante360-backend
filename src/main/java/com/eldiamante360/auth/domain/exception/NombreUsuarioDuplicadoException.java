package com.eldiamante360.auth.domain.exception;

import com.eldiamante360.shared.domain.exception.RecursoDuplicadoException;

public class NombreUsuarioDuplicadoException extends RecursoDuplicadoException {

    public NombreUsuarioDuplicadoException(String username) {
        super("El nombre de usuario '%s' ya esta en uso".formatted(username));
    }
}
