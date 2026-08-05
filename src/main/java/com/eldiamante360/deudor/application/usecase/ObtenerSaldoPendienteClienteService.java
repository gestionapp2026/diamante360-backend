package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.deudor.application.dto.SaldoClienteResult;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Calcula el saldo pendiente total de un cliente, sumando el saldo de todas
 * sus cuentas por cobrar activas (no anuladas).
 */
@Service
@Transactional(readOnly = true)
public class ObtenerSaldoPendienteClienteService implements ObtenerSaldoPendienteClienteUseCase {

    private final CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;

    public ObtenerSaldoPendienteClienteService(CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort,
                                                ClienteRepositoryPort clienteRepositoryPort) {
        this.cuentaPorCobrarRepositoryPort = cuentaPorCobrarRepositoryPort;
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @Override
    public SaldoClienteResult ejecutar(Long clienteId) {
        clienteRepositoryPort.buscarPorId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", clienteId));
        return new SaldoClienteResult(clienteId, cuentaPorCobrarRepositoryPort.sumarSaldoPendientePorCliente(clienteId));
    }
}
