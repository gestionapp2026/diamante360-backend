package com.eldiamante360.inventario.presentation.dto.request;

import com.eldiamante360.inventario.domain.model.TipoMovimiento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RegistrarMovimientoRequest(
        @NotNull(message = "El tipo de movimiento es obligatorio")
        TipoMovimiento tipoMovimiento,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0", message = "La cantidad no puede ser negativa")
        BigDecimal cantidad,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(max = 200, message = "El motivo debe tener maximo 200 caracteres")
        String motivo
) {
}
