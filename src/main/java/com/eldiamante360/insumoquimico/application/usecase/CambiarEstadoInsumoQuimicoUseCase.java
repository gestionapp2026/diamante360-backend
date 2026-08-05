package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;

public interface CambiarEstadoInsumoQuimicoUseCase {

    InsumoQuimicoResult ejecutar(Long insumoId, boolean activo);
}
