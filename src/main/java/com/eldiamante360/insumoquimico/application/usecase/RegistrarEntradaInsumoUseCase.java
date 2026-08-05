package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.MovimientoInsumoResult;
import com.eldiamante360.insumoquimico.application.dto.RegistrarEntradaInsumoCommand;

public interface RegistrarEntradaInsumoUseCase {

    MovimientoInsumoResult ejecutar(RegistrarEntradaInsumoCommand command);
}
