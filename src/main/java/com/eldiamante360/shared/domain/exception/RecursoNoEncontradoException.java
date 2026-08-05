package com.eldiamante360.shared.domain.exception;

public class RecursoNoEncontradoException extends DomainException {

    public RecursoNoEncontradoException(String recurso, Object identificador) {
        super("%s no encontrado: %s".formatted(recurso, identificador));
    }
}
