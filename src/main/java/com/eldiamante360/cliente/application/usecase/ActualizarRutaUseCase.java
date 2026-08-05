package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ActualizarRutaCommand;
import com.eldiamante360.cliente.application.dto.RutaResult;

public interface ActualizarRutaUseCase {

    RutaResult ejecutar(ActualizarRutaCommand command);
}
