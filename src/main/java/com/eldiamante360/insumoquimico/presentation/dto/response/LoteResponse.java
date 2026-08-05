package com.eldiamante360.insumoquimico.presentation.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record LoteResponse(
        Long id,
        Long insumoId,
        String numeroLote,
        LocalDate fechaVencimiento,
        BigDecimal cantidadActual,
        Instant fechaIngreso,
        boolean vencido
) {
}
