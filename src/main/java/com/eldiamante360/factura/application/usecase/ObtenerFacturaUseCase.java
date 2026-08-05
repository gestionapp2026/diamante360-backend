package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.FacturaResult;

public interface ObtenerFacturaUseCase {

    FacturaResult ejecutar(Long id);
}
