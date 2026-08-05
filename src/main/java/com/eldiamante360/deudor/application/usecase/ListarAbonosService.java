package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.AbonoResult;
import com.eldiamante360.deudor.application.port.AbonoRepositoryPort;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarAbonosService implements ListarAbonosUseCase {

    private final AbonoRepositoryPort abonoRepositoryPort;
    private final CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    public ListarAbonosService(AbonoRepositoryPort abonoRepositoryPort,
                                CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort) {
        this.abonoRepositoryPort = abonoRepositoryPort;
        this.cuentaPorCobrarRepositoryPort = cuentaPorCobrarRepositoryPort;
    }

    @Override
    public Page<AbonoResult> ejecutar(Long cuentaPorCobrarId, Pageable pageable) {
        cuentaPorCobrarRepositoryPort.buscarPorId(cuentaPorCobrarId)
                .orElseThrow(() -> new RecursoNoEncontradoException("CuentaPorCobrar", cuentaPorCobrarId));
        return abonoRepositoryPort.listarPorCuenta(cuentaPorCobrarId, pageable).map(CuentaPorCobrarAssembler::toResult);
    }
}
