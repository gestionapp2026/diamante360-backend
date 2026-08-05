package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.MovimientoInsumoResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarMovimientosPorInsumoUseCase {

    Page<MovimientoInsumoResult> ejecutar(Long insumoId, Pageable pageable);
}
