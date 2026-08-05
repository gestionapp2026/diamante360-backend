package com.eldiamante360.orden.application.usecase;

import com.eldiamante360.orden.application.dto.OrdenResult;
import com.eldiamante360.orden.application.port.OrdenRepositoryPort;
import com.eldiamante360.orden.domain.model.Orden;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnularOrdenService implements AnularOrdenUseCase {

    private final OrdenRepositoryPort ordenRepositoryPort;

    public AnularOrdenService(OrdenRepositoryPort ordenRepositoryPort) {
        this.ordenRepositoryPort = ordenRepositoryPort;
    }

    @Override
    public OrdenResult ejecutar(Long ordenId, Long usuarioId) {
        Orden orden = ordenRepositoryPort.buscarPorId(ordenId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden", ordenId));

        orden.anular();

        Orden guardada = ordenRepositoryPort.guardar(orden);
        return OrdenAssembler.toResult(guardada);
    }
}
