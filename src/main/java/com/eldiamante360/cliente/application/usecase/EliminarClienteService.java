package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.deudor.infrastructure.persistence.repository.CuentaPorCobrarJpaRepository;
import com.eldiamante360.factura.infrastructure.persistence.repository.FacturaJpaRepository;
import com.eldiamante360.orden.infrastructure.persistence.repository.OrdenJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarClienteService implements EliminarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final FacturaJpaRepository facturaJpaRepository;
    private final OrdenJpaRepository ordenJpaRepository;
    private final CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository;

    public EliminarClienteService(ClienteRepositoryPort clienteRepositoryPort,
                                   FacturaJpaRepository facturaJpaRepository,
                                   OrdenJpaRepository ordenJpaRepository,
                                   CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.facturaJpaRepository = facturaJpaRepository;
        this.ordenJpaRepository = ordenJpaRepository;
        this.cuentaPorCobrarJpaRepository = cuentaPorCobrarJpaRepository;
    }

    @Override
    public void ejecutar(Long id, boolean cascada) {
        Cliente cliente = clienteRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", id));

        boolean tieneDependencias = facturaJpaRepository.existsByClienteId(cliente.getId())
                || ordenJpaRepository.existsByClienteId(cliente.getId())
                || cuentaPorCobrarJpaRepository.existsByClienteId(cliente.getId());

        if (tieneDependencias && !cascada) {
            throw new RecursoConDependenciasException(
                    "No se puede eliminar: el cliente tiene facturas, ordenes o cuentas por cobrar asociadas.");
        }

        if (cascada) {
            // Las tres dependencias son propiedad exclusiva del cliente: se
            // borran en este orden para respetar las FK (cuenta_por_cobrar
            // referencia factura; detalle_factura/historial_factura y
            // detalle_orden se cascadean solos por FK de BD).
            cuentaPorCobrarJpaRepository.deleteByClienteId(cliente.getId());
            facturaJpaRepository.deleteByClienteId(cliente.getId());
            ordenJpaRepository.deleteByClienteId(cliente.getId());
        }

        clienteRepositoryPort.eliminar(cliente.getId());
    }
}
