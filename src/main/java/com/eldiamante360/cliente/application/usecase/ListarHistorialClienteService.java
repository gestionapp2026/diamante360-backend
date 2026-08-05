package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.HistorialClienteResult;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.HistorialClienteRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarHistorialClienteService implements ListarHistorialClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final HistorialClienteRepositoryPort historialClienteRepositoryPort;

    public ListarHistorialClienteService(ClienteRepositoryPort clienteRepositoryPort,
                                          HistorialClienteRepositoryPort historialClienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.historialClienteRepositoryPort = historialClienteRepositoryPort;
    }

    @Override
    public Page<HistorialClienteResult> ejecutar(Long clienteId, Pageable pageable) {
        clienteRepositoryPort.buscarPorId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", clienteId));

        return historialClienteRepositoryPort.listarPorCliente(clienteId, pageable).map(ClienteAssembler::toResult);
    }
}
