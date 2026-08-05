package com.eldiamante360.formula.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

/**
 * El "numero" de un detalle es el numero del frasco fisico que el operario
 * de planta debe usar; debe ser unico dentro de la formula (no globalmente,
 * ver Formula/DetalleFormula).
 */
public class NumeroDetalleDuplicadoException extends ReglaNegocioException {

    public NumeroDetalleDuplicadoException(Integer numero) {
        super("El numero de frasco %d esta repetido en la formula".formatted(numero));
    }
}
