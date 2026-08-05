package com.eldiamante360.insumoquimico.presentation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RegistrarSalidaInsumoRequest(
        @NotNull(message = "El lote es obligatorio")
        Long loteId,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0", inclusive = false, message = "La cantidad debe ser mayor que cero")
        BigDecimal cantidad,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(max = 200, message = "El motivo debe tener maximo 200 caracteres")
        String motivo
) {
}
