package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.CrearProductoCommand;
import com.eldiamante360.producto.application.dto.ProductoResult;

public interface CrearProductoUseCase {

    ProductoResult ejecutar(CrearProductoCommand command);
}
