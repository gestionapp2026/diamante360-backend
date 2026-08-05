package com.eldiamante360.insumoquimico.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;

public class StockLoteInsuficienteException extends ReglaNegocioException {

    public StockLoteInsuficienteException(Long loteId, BigDecimal cantidadActual, BigDecimal cantidadSolicitada) {
        super("Stock insuficiente en el lote %d: disponible %s, solicitado %s"
                .formatted(loteId, cantidadActual, cantidadSolicitada));
    }
}
