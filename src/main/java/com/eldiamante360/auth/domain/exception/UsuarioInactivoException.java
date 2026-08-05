package com.eldiamante360.auth.domain.exception;

import com.eldiamante360.shared.domain.exception.DomainException;

public class UsuarioInactivoException extends DomainException {

    public UsuarioInactivoException() {
        super("El usuario se encuentra inactivo, contacte a un administrador");
    }
}
