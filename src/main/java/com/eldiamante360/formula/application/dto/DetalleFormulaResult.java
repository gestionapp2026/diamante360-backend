package com.eldiamante360.formula.application.dto;

import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;

import java.math.BigDecimal;

/**
 * {@code insumoNombre} puede venir nulo si el insumo referenciado ya no
 * existe; su enmascaramiento por permisos (rol planta) es responsabilidad
 * de la capa de presentacion, no de este DTO de aplicacion.
 */
public record DetalleFormulaResult(
        Long id,
        Integer numero,
        Long insumoId,
        String insumoNombre,
        BigDecimal cantidad,
        UnidadMedidaInsumo unidadMedidaInsumo
) {
}
