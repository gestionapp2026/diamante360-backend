package com.eldiamante360.deudor.presentation.dto.response;

import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;

import java.math.BigDecimal;
import java.time.Instant;

public record CuentaPorCobrarResponse(
        Long id,
        Long facturaId,
        String numeroFactura,
        Long clienteId,
        String clienteNombre,
        String clienteNumeroDocumento,
        BigDecimal montoOriginal,
        BigDecimal saldoPendiente,
        EstadoCuentaPorCobrar estado,
        Long usuarioId,
        Instant fecha,
        Instant fechaUltimoAbono,
        Instant fechaAnulacion
) {
}
