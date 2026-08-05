package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;

public interface ObtenerCuentaPorCobrarUseCase {

    CuentaPorCobrarResult ejecutar(Long id);
}
