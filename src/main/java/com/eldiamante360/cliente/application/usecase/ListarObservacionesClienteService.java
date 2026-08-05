package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ObservacionClienteResult;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.ObservacionClienteRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarObservacionesClienteService implements ListarObservacionesClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ObservacionClienteRepositoryPort observacionClienteRepositoryPort;

    public ListarObservacionesClienteService(ClienteRepositoryPort clienteRepositoryPort,
                                              ObservacionClienteRepositoryPort observacionClienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.observacionClienteRepositoryPort = observacionClienteRepositoryPort;
    }

    @Override
    public Page<ObservacionClienteResult> ejecutar(Long clienteId, Pageable pageable) {
        clienteRepositoryPort.buscarPorId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", clienteId));

        return observacionClienteRepositoryPort.listarPorCliente(clienteId, pageable).map(ClienteAssembler::toResult);
    }
}
