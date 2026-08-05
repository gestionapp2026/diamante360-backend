package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ProductoResult;

public interface ObtenerProductoUseCase {

    ProductoResult ejecutar(Long productoId);
}
