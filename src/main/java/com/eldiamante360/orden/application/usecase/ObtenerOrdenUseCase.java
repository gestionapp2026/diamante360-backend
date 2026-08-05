package com.eldiamante360.orden.application.usecase;

import com.eldiamante360.orden.application.dto.OrdenResult;

public interface ObtenerOrdenUseCase {

    OrdenResult ejecutar(Long id);
}
