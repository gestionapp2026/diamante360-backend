package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.dto.RegistrarCreditoCommand;

public interface RegistrarCreditoUseCase {

    CuentaPorCobrarResult ejecutar(RegistrarCreditoCommand command);
}
