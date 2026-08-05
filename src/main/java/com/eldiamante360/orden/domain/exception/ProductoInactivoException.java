package com.eldiamante360.orden.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class ProductoInactivoException extends ReglaNegocioException {

    public ProductoInactivoException(String nombreProducto) {
        super("No se puede incluir el producto '%s' en la orden porque esta inactivo".formatted(nombreProducto));
    }
}
