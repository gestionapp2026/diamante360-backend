package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;

public interface ObtenerCuentaPorCobrarPorFacturaUseCase {

    CuentaPorCobrarResult ejecutar(Long facturaId);
}
