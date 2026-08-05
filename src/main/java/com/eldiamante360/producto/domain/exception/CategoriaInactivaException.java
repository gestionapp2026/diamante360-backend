package com.eldiamante360.producto.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class CategoriaInactivaException extends ReglaNegocioException {

    public CategoriaInactivaException(String nombreCategoria) {
        super("La categoria '%s' esta inactiva, no se pueden asignar productos nuevos".formatted(nombreCategoria));
    }
}
