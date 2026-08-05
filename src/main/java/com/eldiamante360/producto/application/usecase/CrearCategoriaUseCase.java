package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.CategoriaResult;
import com.eldiamante360.producto.application.dto.CrearCategoriaCommand;

public interface CrearCategoriaUseCase {

    CategoriaResult ejecutar(CrearCategoriaCommand command);
}
