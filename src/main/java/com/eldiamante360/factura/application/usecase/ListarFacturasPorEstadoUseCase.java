package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.domain.model.EstadoFactura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarFacturasPorEstadoUseCase {

    Page<FacturaResult> ejecutar(EstadoFactura estado, Pageable pageable);
}
