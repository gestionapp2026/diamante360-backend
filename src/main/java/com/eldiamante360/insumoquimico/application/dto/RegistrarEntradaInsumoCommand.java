package com.eldiamante360.insumoquimico.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistrarEntradaInsumoCommand(
        Long insumoId,
        String numeroLote,
        LocalDate fechaVencimiento,
        BigDecimal cantidad,
        String motivo,
        Long usuarioId
) {
}
