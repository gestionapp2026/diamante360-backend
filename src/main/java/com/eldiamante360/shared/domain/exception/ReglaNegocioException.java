package com.eldiamante360.shared.domain.exception;

/**
 * Violacion de una regla de negocio que no encaja en un caso mas especifico
 * (duplicado, no encontrado, etc). El mensaje debe ser entendible por el
 * usuario final ya que se propaga tal cual en la respuesta HTTP.
 */
public class ReglaNegocioException extends DomainException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
