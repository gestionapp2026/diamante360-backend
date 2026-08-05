package com.eldiamante360.formula.presentation.dto.response;

import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;

import java.math.BigDecimal;

/**
 * {@code insumoNombre} viene {@code null} si el usuario autenticado no tiene
 * el permiso {@code INSUMO_VER_NOMBRE} (por ejemplo, el rol PLANTA): solo
 * debe ver el numero de frasco, nunca el nombre del quimico.
 */
public record DetalleFormulaResponse(
        Long id,
        Integer numero,
        Long insumoId,
        String insumoNombre,
        BigDecimal cantidad,
        UnidadMedidaInsumo unidadMedidaInsumo
) {
}
