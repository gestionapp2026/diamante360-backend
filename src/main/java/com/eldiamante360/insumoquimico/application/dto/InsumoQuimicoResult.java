package com.eldiamante360.insumoquimico.application.dto;

import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;

import java.math.BigDecimal;

public record InsumoQuimicoResult(
        Long id,
        String nombre,
        UnidadMedidaInsumo unidadMedida,
        BigDecimal stockActual,
        boolean activo,
        BigDecimal precioCompra
) {
}
