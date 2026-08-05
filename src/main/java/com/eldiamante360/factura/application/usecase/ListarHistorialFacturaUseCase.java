package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.HistorialFacturaResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarHistorialFacturaUseCase {

    Page<HistorialFacturaResult> ejecutar(Long facturaId, Pageable pageable);
}
