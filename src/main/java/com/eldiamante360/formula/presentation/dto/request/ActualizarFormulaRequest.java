package com.eldiamante360.formula.presentation.dto.request;

import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ActualizarFormulaRequest(
        @NotNull(message = "La cantidad base es obligatoria")
        @DecimalMin(value = "0", inclusive = false, message = "La cantidad base debe ser mayor a cero")
        BigDecimal cantidadBase,

        @NotNull(message = "La unidad base es obligatoria")
        UnidadMedidaInsumo unidadBase,

        @NotEmpty(message = "La formula debe tener al menos un detalle")
        List<@Valid DetalleFormulaRequest> detalles
) {
}
