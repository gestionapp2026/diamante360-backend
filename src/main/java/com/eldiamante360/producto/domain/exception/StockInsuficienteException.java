package com.eldiamante360.producto.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;

public class StockInsuficienteException extends ReglaNegocioException {

    public StockInsuficienteException(BigDecimal stockActual, BigDecimal cantidadSolicitada) {
        super("Stock insuficiente: disponible %s, solicitado %s".formatted(stockActual, cantidadSolicitada));
    }
}
