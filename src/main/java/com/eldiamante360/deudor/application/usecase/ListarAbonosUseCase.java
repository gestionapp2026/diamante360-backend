package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.AbonoResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarAbonosUseCase {

    Page<AbonoResult> ejecutar(Long cuentaPorCobrarId, Pageable pageable);
}
