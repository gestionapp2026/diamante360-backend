package com.eldiamante360.inventario.application.usecase;

import com.eldiamante360.inventario.application.dto.MovimientoResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarMovimientosPorProductoUseCase {

    Page<MovimientoResult> ejecutar(Long productoId, Pageable pageable);
}
