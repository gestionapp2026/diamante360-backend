package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarCuentasPorCobrarPorEstadoService implements ListarCuentasPorCobrarPorEstadoUseCase {

    private final CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    public ListarCuentasPorCobrarPorEstadoService(CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort) {
        this.cuentaPorCobrarRepositoryPort = cuentaPorCobrarRepositoryPort;
    }

    @Override
    public Page<CuentaPorCobrarResult> ejecutar(EstadoCuentaPorCobrar estado, Pageable pageable) {
        return cuentaPorCobrarRepositoryPort.listarPorEstado(estado, pageable).map(CuentaPorCobrarAssembler::toResult);
    }
}
