package com.eldiamante360.insumoquimico.presentation.dto.request;

import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CrearInsumoQuimicoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre debe tener maximo 120 caracteres")
        String nombre,

        @NotNull(message = "La unidad de medida es obligatoria")
        UnidadMedidaInsumo unidadMedida,

        @DecimalMin(value = "0", message = "El precio de compra no puede ser negativo")
        BigDecimal precioCompra
) {

    public CrearInsumoQuimicoRequest(String nombre, UnidadMedidaInsumo unidadMedida) {
        this(nombre, unidadMedida, null);
    }
}
