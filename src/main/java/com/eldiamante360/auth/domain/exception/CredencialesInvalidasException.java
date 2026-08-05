package com.eldiamante360.auth.domain.exception;

import com.eldiamante360.shared.domain.exception.DomainException;

public class CredencialesInvalidasException extends DomainException {

    public CredencialesInvalidasException() {
        super("Usuario o contrasena invalidos");
    }
}
