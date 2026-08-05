package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarInsumosQuimicosUseCase {

    Page<InsumoQuimicoResult> ejecutar(Pageable pageable);
}
