package com.eldiamante360.producto.presentation.dto.request;

import com.eldiamante360.producto.domain.model.TipoVenta;
import com.eldiamante360.producto.domain.model.UnidadMedida;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CrearProductoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre debe tener maximo 120 caracteres")
        String nombre,

        @NotNull(message = "La categoria es obligatoria")
        Long categoriaId,

        @NotNull(message = "El tipo de venta es obligatorio")
        TipoVenta tipoVenta,

        @NotNull(message = "La unidad de medida es obligatoria")
        UnidadMedida unidadMedida,

        @NotNull(message = "El precio de compra es obligatorio")
        @DecimalMin(value = "0", message = "El precio de compra no puede ser negativo")
        BigDecimal precioCompra,

        @NotNull(message = "El precio de venta es obligatorio")
        @DecimalMin(value = "0", message = "El precio de venta no puede ser negativo")
        BigDecimal precioVenta,

        @NotNull(message = "El stock inicial es obligatorio")
        @DecimalMin(value = "0", message = "El stock inicial no puede ser negativo")
        BigDecimal stockInicial,

        @NotNull(message = "El stock minimo es obligatorio")
        @DecimalMin(value = "0", message = "El stock minimo no puede ser negativo")
        BigDecimal stockMinimo
) {
}
