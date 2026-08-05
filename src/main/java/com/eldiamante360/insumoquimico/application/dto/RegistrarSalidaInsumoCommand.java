package com.eldiamante360.insumoquimico.application.dto;

import java.math.BigDecimal;

public record RegistrarSalidaInsumoCommand(
        Long insumoId,
        Long loteId,
        BigDecimal cantidad,
        String motivo,
        Long usuarioId
) {
}
