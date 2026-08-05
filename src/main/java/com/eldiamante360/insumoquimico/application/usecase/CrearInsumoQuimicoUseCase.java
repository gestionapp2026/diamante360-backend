package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.CrearInsumoQuimicoCommand;
import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;

public interface CrearInsumoQuimicoUseCase {

    InsumoQuimicoResult ejecutar(CrearInsumoQuimicoCommand command);
}
