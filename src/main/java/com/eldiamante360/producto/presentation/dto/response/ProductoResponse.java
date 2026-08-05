package com.eldiamante360.producto.presentation.dto.response;

import com.eldiamante360.producto.domain.model.TipoVenta;
import com.eldiamante360.producto.domain.model.UnidadMedida;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        Long categoriaId,
        String categoriaNombre,
        TipoVenta tipoVenta,
        UnidadMedida unidadMedida,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        BigDecimal stockActual,
        BigDecimal stockMinimo,
        boolean stockBajo,
        boolean activo
) {
}
