package com.eldiamante360.producto.application.dto;

import com.eldiamante360.producto.domain.model.TipoVenta;
import com.eldiamante360.producto.domain.model.UnidadMedida;

import java.math.BigDecimal;

public record CrearProductoCommand(
        String nombre,
        Long categoriaId,
        TipoVenta tipoVenta,
        UnidadMedida unidadMedida,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        BigDecimal stockInicial,
        BigDecimal stockMinimo
) {
}
