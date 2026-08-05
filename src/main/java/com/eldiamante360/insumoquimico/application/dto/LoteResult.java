package com.eldiamante360.insumoquimico.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record LoteResult(
        Long id,
        Long insumoId,
        String numeroLote,
        LocalDate fechaVencimiento,
        BigDecimal cantidadActual,
        Instant fechaIngreso,
        boolean vencido
) {
}
