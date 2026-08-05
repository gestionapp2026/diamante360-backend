package com.eldiamante360.deudor.application.dto;

import java.math.BigDecimal;

public record RegistrarCreditoCommand(
        Long facturaId,
        String numeroFactura,
        Long clienteId,
        String clienteNombre,
        String clienteNumeroDocumento,
        BigDecimal monto,
        Long usuarioId
) {
}
