package com.eldiamante360.inventario.application.usecase;

import com.eldiamante360.inventario.application.dto.MovimientoResult;
import com.eldiamante360.inventario.application.dto.RegistrarMovimientoCommand;

public interface RegistrarMovimientoInventarioUseCase {

    MovimientoResult ejecutar(RegistrarMovimientoCommand command);
}
