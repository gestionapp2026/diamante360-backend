package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.CrearRutaCommand;
import com.eldiamante360.cliente.application.dto.RutaResult;

public interface CrearRutaUseCase {

    RutaResult ejecutar(CrearRutaCommand command);
}
