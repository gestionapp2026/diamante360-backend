package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.RutaResult;

public interface CambiarEstadoRutaUseCase {

    RutaResult ejecutar(Long rutaId, boolean activo);
}
