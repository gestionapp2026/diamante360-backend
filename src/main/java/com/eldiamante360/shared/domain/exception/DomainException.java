package com.eldiamante360.shared.domain.exception;

/**
 * Excepcion base para todas las reglas de negocio del dominio.
 * Ninguna clase de dominio depende de Spring ni de ningun framework.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
