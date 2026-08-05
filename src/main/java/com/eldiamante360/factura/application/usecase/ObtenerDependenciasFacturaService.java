package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.deudor.infrastructure.persistence.repository.CuentaPorCobrarJpaRepository;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import com.eldiamante360.shared.presentation.DependenciasResponse.ConteoDependencia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ObtenerDependenciasFacturaService implements ObtenerDependenciasFacturaUseCase {

    static final String MENSAJE_BLOQUEO_NO_ANULADA =
            "No se puede eliminar: la factura debe estar anulada antes de borrarla.";

    private final FacturaRepositoryPort facturaRepositoryPort;
    private final CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository;

    public ObtenerDependenciasFacturaService(FacturaRepositoryPort facturaRepositoryPort,
                                              CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository) {
        this.facturaRepositoryPort = facturaRepositoryPort;
        this.cuentaPorCobrarJpaRepository = cuentaPorCobrarJpaRepository;
    }

    @Override
    public DependenciasResponse ejecutar(Long id) {
        var factura = facturaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Factura", id));

        long cuentasPorCobrar = cuentaPorCobrarJpaRepository.countByFacturaId(factura.getId());

        List<ConteoDependencia> conteos = new ArrayList<>();
        if (cuentasPorCobrar > 0) {
            conteos.add(new ConteoDependencia("cuentasPorCobrar", "Cuentas por cobrar", cuentasPorCobrar));
        }

        boolean bloqueado = !factura.estaAnulada();
        String mensajeBloqueo = bloqueado ? MENSAJE_BLOQUEO_NO_ANULADA : null;

        boolean tieneDependencias = !conteos.isEmpty();
        return new DependenciasResponse(tieneDependencias, bloqueado, mensajeBloqueo, conteos);
    }
}
