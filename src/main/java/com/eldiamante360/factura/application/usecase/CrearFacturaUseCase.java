package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.CrearFacturaCommand;
import com.eldiamante360.factura.application.dto.FacturaResult;

public interface CrearFacturaUseCase {

    FacturaResult ejecutar(CrearFacturaCommand command);
}
