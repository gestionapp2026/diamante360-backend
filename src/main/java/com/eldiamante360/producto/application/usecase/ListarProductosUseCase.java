package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ProductoResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarProductosUseCase {

    Page<ProductoResult> ejecutar(Pageable pageable);
}
