package com.eldiamante360.orden.application.usecase;

import com.eldiamante360.orden.application.dto.OrdenFiltro;
import com.eldiamante360.orden.application.dto.OrdenResult;
import com.eldiamante360.orden.application.port.OrdenRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarOrdenesService implements ListarOrdenesUseCase {

    private final OrdenRepositoryPort ordenRepositoryPort;

    public ListarOrdenesService(OrdenRepositoryPort ordenRepositoryPort) {
        this.ordenRepositoryPort = ordenRepositoryPort;
    }

    @Override
    public Page<OrdenResult> ejecutar(OrdenFiltro filtro, Pageable pageable) {
        return ordenRepositoryPort.listar(filtro, pageable).map(OrdenAssembler::toResult);
    }
}
