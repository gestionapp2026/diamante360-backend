package com.eldiamante360.formula.presentation.dto.response;

import java.math.BigDecimal;

/**
 * Deliberadamente sin nombre de insumo: la respuesta de produccion siempre
 * solo muestra el numero de frasco, independientemente de los permisos del
 * usuario (el operario de planta es el usuario tipico de este endpoint).
 */
public record ConsumoInsumoResponse(
        Long insumoId,
        Integer numero,
        BigDecimal cantidadConsumida
) {
}
