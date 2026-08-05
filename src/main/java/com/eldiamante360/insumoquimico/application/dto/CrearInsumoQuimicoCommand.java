package com.eldiamante360.insumoquimico.application.dto;

import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;

import java.math.BigDecimal;

public record CrearInsumoQuimicoCommand(
        String nombre,
        UnidadMedidaInsumo unidadMedida,
        BigDecimal precioCompra
) {

    public CrearInsumoQuimicoCommand(String nombre, UnidadMedidaInsumo unidadMedida) {
        this(nombre, unidadMedida, null);
    }
}
