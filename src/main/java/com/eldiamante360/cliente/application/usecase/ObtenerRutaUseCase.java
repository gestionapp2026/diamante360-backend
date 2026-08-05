package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.RutaResult;

public interface ObtenerRutaUseCase {

    RutaResult ejecutar(Long rutaId);
}
