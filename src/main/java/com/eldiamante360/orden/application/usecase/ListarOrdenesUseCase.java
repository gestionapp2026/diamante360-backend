package com.eldiamante360.orden.application.usecase;

import com.eldiamante360.orden.application.dto.OrdenFiltro;
import com.eldiamante360.orden.application.dto.OrdenResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarOrdenesUseCase {

    Page<OrdenResult> ejecutar(OrdenFiltro filtro, Pageable pageable);
}
