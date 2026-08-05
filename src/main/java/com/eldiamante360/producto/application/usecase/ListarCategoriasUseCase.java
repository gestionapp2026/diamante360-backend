package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.CategoriaResult;

import java.util.List;

public interface ListarCategoriasUseCase {

    List<CategoriaResult> ejecutar();
}
