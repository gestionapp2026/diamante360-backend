package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ProductoResult;

public interface CambiarEstadoProductoUseCase {

    ProductoResult ejecutar(Long productoId, boolean activo);
}
