package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.dto.RegistrarAbonoCommand;

public interface RegistrarAbonoUseCase {

    CuentaPorCobrarResult ejecutar(RegistrarAbonoCommand command);
}
