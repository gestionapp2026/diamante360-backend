package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarFacturasPorClienteService implements ListarFacturasPorClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final FacturaRepositoryPort facturaRepositoryPort;

    public ListarFacturasPorClienteService(ClienteRepositoryPort clienteRepositoryPort,
                                            FacturaRepositoryPort facturaRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.facturaRepositoryPort = facturaRepositoryPort;
    }

    @Override
    public Page<FacturaResult> ejecutar(Long clienteId, Pageable pageable) {
        clienteRepositoryPort.buscarPorId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", clienteId));

        return facturaRepositoryPort.listarPorCliente(clienteId, pageable).map(FacturaAssembler::toResult);
    }
}
