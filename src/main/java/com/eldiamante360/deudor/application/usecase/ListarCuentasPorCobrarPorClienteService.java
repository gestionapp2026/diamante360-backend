package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarCuentasPorCobrarPorClienteService implements ListarCuentasPorCobrarPorClienteUseCase {

    private final CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;

    public ListarCuentasPorCobrarPorClienteService(CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort,
                                                     ClienteRepositoryPort clienteRepositoryPort) {
        this.cuentaPorCobrarRepositoryPort = cuentaPorCobrarRepositoryPort;
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @Override
    public Page<CuentaPorCobrarResult> ejecutar(Long clienteId, Pageable pageable) {
        clienteRepositoryPort.buscarPorId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", clienteId));
        return cuentaPorCobrarRepositoryPort.listarPorCliente(clienteId, pageable).map(CuentaPorCobrarAssembler::toResult);
    }
}
