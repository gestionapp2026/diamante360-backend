package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BuscarClientesService implements BuscarClientesUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public BuscarClientesService(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @Override
    public Page<ClienteResult> ejecutar(String texto, Pageable pageable) {
        return clienteRepositoryPort.buscar(texto, pageable).map(ClienteAssembler::toResult);
    }
}
