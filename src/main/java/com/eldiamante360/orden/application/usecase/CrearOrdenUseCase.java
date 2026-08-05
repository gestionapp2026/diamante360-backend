package com.eldiamante360.orden.application.usecase;

import com.eldiamante360.orden.application.dto.CrearOrdenCommand;
import com.eldiamante360.orden.application.dto.OrdenResult;

public interface CrearOrdenUseCase {

    OrdenResult ejecutar(CrearOrdenCommand command);
}
