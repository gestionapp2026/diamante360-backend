package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarCuentasPorCobrarPorClienteUseCase {

    Page<CuentaPorCobrarResult> ejecutar(Long clienteId, Pageable pageable);
}
