package com.eldiamante360.formula.application.dto;

import java.math.BigDecimal;

/**
 * Resumen de cuanto se consumio de un insumo al producir con una formula.
 * Deliberadamente NO incluye el nombre del insumo: solo el numero de frasco
 * e id, para que el rol planta nunca vea el nombre del quimico.
 */
public record ConsumoInsumoResult(
        Long insumoId,
        Integer numero,
        BigDecimal cantidadConsumida
) {
}
