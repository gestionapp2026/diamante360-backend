package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.FacturaResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarFacturasUseCase {

    Page<FacturaResult> ejecutar(Pageable pageable);
}
