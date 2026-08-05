package com.eldiamante360.formula.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;

/**
 * Se lanza al intentar producir con una formula cuando alguno de los
 * quimicos requeridos no tiene stock suficiente. Solo esta excepcion (capa
 * de aplicacion/dominio) conoce el nombre del quimico involucrado; la
 * mascara de nombres para el rol planta es una preocupacion de presentacion
 * y no aplica a los mensajes de error internos.
 */
public class StockInsumoInsuficienteParaFormulaException extends ReglaNegocioException {

    public StockInsumoInsuficienteParaFormulaException(String nombreInsumo, BigDecimal stockDisponible,
                                                         BigDecimal cantidadRequerida) {
        super("Stock insuficiente del insumo '%s' para producir: disponible %s, requerido %s"
                .formatted(nombreInsumo, stockDisponible, cantidadRequerida));
    }
}
