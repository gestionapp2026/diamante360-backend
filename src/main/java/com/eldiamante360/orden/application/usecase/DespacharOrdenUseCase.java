package com.eldiamante360.orden.application.usecase;

import com.eldiamante360.orden.application.dto.OrdenResult;

public interface DespacharOrdenUseCase {

    OrdenResult ejecutar(Long ordenId, Long usuarioId);
}
