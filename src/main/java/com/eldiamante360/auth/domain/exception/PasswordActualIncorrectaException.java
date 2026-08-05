package com.eldiamante360.auth.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class PasswordActualIncorrectaException extends ReglaNegocioException {

    public PasswordActualIncorrectaException() {
        super("La contrasena actual ingresada no es correcta");
    }
}
