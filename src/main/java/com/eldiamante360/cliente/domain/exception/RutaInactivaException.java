package com.eldiamante360.cliente.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class RutaInactivaException extends ReglaNegocioException {

    public RutaInactivaException(String nombreRuta) {
        super("La ruta '%s' esta inactiva, no se puede asignar a un cliente".formatted(nombreRuta));
    }
}
