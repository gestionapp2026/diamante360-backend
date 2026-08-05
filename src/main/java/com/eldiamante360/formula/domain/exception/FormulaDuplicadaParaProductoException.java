package com.eldiamante360.formula.domain.exception;

import com.eldiamante360.shared.domain.exception.RecursoDuplicadoException;

/**
 * Regla de negocio: solo puede existir una formula (receta) por producto.
 */
public class FormulaDuplicadaParaProductoException extends RecursoDuplicadoException {

    public FormulaDuplicadaParaProductoException(Long productoId) {
        super("Ya existe una formula para el producto %d".formatted(productoId));
    }
}
