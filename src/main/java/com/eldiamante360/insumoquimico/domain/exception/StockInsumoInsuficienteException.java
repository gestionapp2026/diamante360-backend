package com.eldiamante360.insumoquimico.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;

public class StockInsumoInsuficienteException extends ReglaNegocioException {

    public StockInsumoInsuficienteException(BigDecimal stockActual, BigDecimal cantidadSolicitada) {
        super("Stock insuficiente del insumo: disponible %s, solicitado %s".formatted(stockActual, cantidadSolicitada));
    }
}
