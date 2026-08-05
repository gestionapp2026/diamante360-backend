package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.LoteResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarLotesPorInsumoUseCase {

    Page<LoteResult> ejecutar(Long insumoId, Pageable pageable);
}
