package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.HistorialCuentaPorCobrarResult;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.application.port.HistorialCuentaPorCobrarRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarHistorialCuentaPorCobrarService implements ListarHistorialCuentaPorCobrarUseCase {

    private final HistorialCuentaPorCobrarRepositoryPort historialCuentaPorCobrarRepositoryPort;
    private final CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    public ListarHistorialCuentaPorCobrarService(HistorialCuentaPorCobrarRepositoryPort historialCuentaPorCobrarRepositoryPort,
                                                  CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort) {
        this.historialCuentaPorCobrarRepositoryPort = historialCuentaPorCobrarRepositoryPort;
        this.cuentaPorCobrarRepositoryPort = cuentaPorCobrarRepositoryPort;
    }

    @Override
    public Page<HistorialCuentaPorCobrarResult> ejecutar(Long cuentaPorCobrarId, Pageable pageable) {
        cuentaPorCobrarRepositoryPort.buscarPorId(cuentaPorCobrarId)
                .orElseThrow(() -> new RecursoNoEncontradoException("CuentaPorCobrar", cuentaPorCobrarId));
        return historialCuentaPorCobrarRepositoryPort.listarPorCuenta(cuentaPorCobrarId, pageable)
                .map(CuentaPorCobrarAssembler::toResult);
    }
}
