package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObtenerCuentaPorCobrarService implements ObtenerCuentaPorCobrarUseCase {

    private final CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    public ObtenerCuentaPorCobrarService(CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort) {
        this.cuentaPorCobrarRepositoryPort = cuentaPorCobrarRepositoryPort;
    }

    @Override
    public CuentaPorCobrarResult ejecutar(Long id) {
        CuentaPorCobrar cuenta = cuentaPorCobrarRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("CuentaPorCobrar", id));
        return CuentaPorCobrarAssembler.toResult(cuenta);
    }
}
