package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;

public interface AnularCuentaPorCobrarUseCase {

    CuentaPorCobrarResult ejecutar(Long facturaId, Long usuarioId);
}
