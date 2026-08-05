package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.CategoriaResult;

public interface CambiarEstadoCategoriaUseCase {

    CategoriaResult ejecutar(Long categoriaId, boolean activo);
}
