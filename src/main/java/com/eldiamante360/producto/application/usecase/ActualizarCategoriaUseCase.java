package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ActualizarCategoriaCommand;
import com.eldiamante360.producto.application.dto.CategoriaResult;

public interface ActualizarCategoriaUseCase {

    CategoriaResult ejecutar(ActualizarCategoriaCommand command);
}
