package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.HistorialCuentaPorCobrarResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarHistorialCuentaPorCobrarUseCase {

    Page<HistorialCuentaPorCobrarResult> ejecutar(Long cuentaPorCobrarId, Pageable pageable);
}
