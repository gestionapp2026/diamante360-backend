package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.MovimientoInsumoResult;
import com.eldiamante360.insumoquimico.application.dto.RegistrarSalidaInsumoCommand;

public interface RegistrarSalidaInsumoUseCase {

    MovimientoInsumoResult ejecutar(RegistrarSalidaInsumoCommand command);
}
