package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.deudor.infrastructure.persistence.repository.CuentaPorCobrarJpaRepository;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.factura.domain.model.Factura;
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarFacturaService implements EliminarFacturaUseCase {

    private final FacturaRepositoryPort facturaRepositoryPort;
    private final CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository;

    public EliminarFacturaService(FacturaRepositoryPort facturaRepositoryPort,
                                   CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository) {
        this.facturaRepositoryPort = facturaRepositoryPort;
        this.cuentaPorCobrarJpaRepository = cuentaPorCobrarJpaRepository;
    }

    @Override
    public void ejecutar(Long id, boolean cascada) {
        Factura factura = facturaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Factura", id));

        if (!factura.estaAnulada()) {
            throw new RecursoConDependenciasException(
                    "No se puede eliminar: la factura debe estar anulada antes de borrarla.");
        }

        boolean tieneCuentaPorCobrar = cuentaPorCobrarJpaRepository.existsByFacturaId(factura.getId());

        if (tieneCuentaPorCobrar && !cascada) {
            throw new RecursoConDependenciasException(
                    "No se puede eliminar: la factura tiene una cuenta por cobrar asociada.");
        }

        if (cascada && tieneCuentaPorCobrar) {
            cuentaPorCobrarJpaRepository.deleteByFacturaId(factura.getId());
        }

        facturaRepositoryPort.eliminar(factura.getId());
    }
}
