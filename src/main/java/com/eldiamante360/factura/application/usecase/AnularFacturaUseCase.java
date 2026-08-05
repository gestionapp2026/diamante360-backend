package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.FacturaResult;

public interface AnularFacturaUseCase {

    FacturaResult ejecutar(Long facturaId, Long usuarioId);
}
