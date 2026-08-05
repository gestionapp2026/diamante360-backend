package com.eldiamante360.formula.domain.model;

import com.eldiamante360.formula.domain.exception.CantidadDetalleFormulaInvalidaException;

import java.math.BigDecimal;

/**
 * Linea de una formula: cuanto de un insumo quimico (identificado por
 * {@code insumoId}) se necesita por cada {@code cantidadBase} del producto.
 * El {@code numero} es el numero del frasco fisico que el operario de planta
 * debe usar; es unico solo dentro de esta formula (no es una identidad
 * global del quimico: el mismo numero puede representar un quimico distinto
 * en otra formula).
 */
public record DetalleFormula(
        Long id,
        Long insumoId,
        Integer numero,
        BigDecimal cantidad
) {

    public static DetalleFormula nuevo(Long insumoId, Integer numero, BigDecimal cantidad) {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CantidadDetalleFormulaInvalidaException();
        }
        return new DetalleFormula(null, insumoId, numero, cantidad);
    }
}
