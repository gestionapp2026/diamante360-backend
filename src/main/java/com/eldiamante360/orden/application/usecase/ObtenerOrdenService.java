package com.eldiamante360.orden.application.usecase;

import com.eldiamante360.orden.application.dto.OrdenResult;
import com.eldiamante360.orden.application.port.OrdenRepositoryPort;
import com.eldiamante360.orden.domain.model.Orden;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObtenerOrdenService implements ObtenerOrdenUseCase {

    private final OrdenRepositoryPort ordenRepositoryPort;

    public ObtenerOrdenService(OrdenRepositoryPort ordenRepositoryPort) {
        this.ordenRepositoryPort = ordenRepositoryPort;
    }

    @Override
    public OrdenResult ejecutar(Long id) {
        Orden orden = ordenRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden", id));
        return OrdenAssembler.toResult(orden);
    }
}
