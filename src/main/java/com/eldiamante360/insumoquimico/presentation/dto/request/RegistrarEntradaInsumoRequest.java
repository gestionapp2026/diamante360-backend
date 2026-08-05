package com.eldiamante360.insumoquimico.presentation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistrarEntradaInsumoRequest(
        @Size(max = 60, message = "El numero de lote debe tener maximo 60 caracteres")
        String numeroLote,

        LocalDate fechaVencimiento,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0", inclusive = false, message = "La cantidad debe ser mayor que cero")
        BigDecimal cantidad,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(max = 200, message = "El motivo debe tener maximo 200 caracteres")
        String motivo
) {
}
