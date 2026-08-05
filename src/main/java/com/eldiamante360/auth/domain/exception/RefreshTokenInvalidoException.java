package com.eldiamante360.auth.domain.exception;

import com.eldiamante360.shared.domain.exception.DomainException;

public class RefreshTokenInvalidoException extends DomainException {

    public RefreshTokenInvalidoException(String mensaje) {
        super(mensaje);
    }
}
