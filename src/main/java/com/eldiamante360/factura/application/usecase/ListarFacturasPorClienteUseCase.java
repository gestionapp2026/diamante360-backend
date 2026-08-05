package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.FacturaResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarFacturasPorClienteUseCase {

    Page<FacturaResult> ejecutar(Long clienteId, Pageable pageable);
}
