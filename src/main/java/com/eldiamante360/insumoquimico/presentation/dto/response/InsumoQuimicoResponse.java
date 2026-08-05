package com.eldiamante360.insumoquimico.presentation.dto.response;

import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;

import java.math.BigDecimal;

public record InsumoQuimicoResponse(
        Long id,
        String nombre,
        UnidadMedidaInsumo unidadMedida,
        BigDecimal stockActual,
        boolean activo,
        BigDecimal precioCompra
) {
}
