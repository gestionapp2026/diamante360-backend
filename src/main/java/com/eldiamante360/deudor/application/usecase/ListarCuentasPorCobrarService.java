package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarCuentasPorCobrarService implements ListarCuentasPorCobrarUseCase {

    private final CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    public ListarCuentasPorCobrarService(CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort) {
        this.cuentaPorCobrarRepositoryPort = cuentaPorCobrarRepositoryPort;
    }

    @Override
    public Page<CuentaPorCobrarResult> ejecutar(Pageable pageable) {
        return cuentaPorCobrarRepositoryPort.listar(pageable).map(CuentaPorCobrarAssembler::toResult);
    }
}
