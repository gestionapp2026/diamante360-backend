package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ProductoResult;

import java.util.List;

public interface ListarProductosStockBajoUseCase {

    List<ProductoResult> ejecutar();
}
