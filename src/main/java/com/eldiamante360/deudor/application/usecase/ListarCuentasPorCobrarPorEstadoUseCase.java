package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarCuentasPorCobrarPorEstadoUseCase {

    Page<CuentaPorCobrarResult> ejecutar(EstadoCuentaPorCobrar estado, Pageable pageable);
}
