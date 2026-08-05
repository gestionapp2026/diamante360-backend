package com.eldiamante360.factura.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class ProductoInactivoException extends ReglaNegocioException {

    public ProductoInactivoException(String nombreProducto) {
        super("No se puede facturar el producto '%s' porque esta inactivo".formatted(nombreProducto));
    }
}
