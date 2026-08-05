package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ActualizarProductoCommand;
import com.eldiamante360.producto.application.dto.ProductoResult;

public interface ActualizarProductoUseCase {

    ProductoResult ejecutar(ActualizarProductoCommand command);
}
