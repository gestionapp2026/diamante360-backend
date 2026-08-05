package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.deudor.infrastructure.persistence.repository.CuentaPorCobrarJpaRepository;
import com.eldiamante360.factura.infrastructure.persistence.repository.FacturaJpaRepository;
import com.eldiamante360.orden.infrastructure.persistence.repository.OrdenJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import com.eldiamante360.shared.presentation.DependenciasResponse.ConteoDependencia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ObtenerDependenciasClienteService implements ObtenerDependenciasClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final FacturaJpaRepository facturaJpaRepository;
    private final OrdenJpaRepository ordenJpaRepository;
    private final CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository;

    public ObtenerDependenciasClienteService(ClienteRepositoryPort clienteRepositoryPort,
                                              FacturaJpaRepository facturaJpaRepository,
                                              OrdenJpaRepository ordenJpaRepository,
                                              CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.facturaJpaRepository = facturaJpaRepository;
        this.ordenJpaRepository = ordenJpaRepository;
        this.cuentaPorCobrarJpaRepository = cuentaPorCobrarJpaRepository;
    }

    @Override
    public DependenciasResponse ejecutar(Long id) {
        var cliente = clienteRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", id));

        long facturas = facturaJpaRepository.countByClienteId(cliente.getId());
        long ordenes = ordenJpaRepository.countByClienteId(cliente.getId());
        long cuentasPorCobrar = cuentaPorCobrarJpaRepository.countByClienteId(cliente.getId());

        List<ConteoDependencia> conteos = new ArrayList<>();
        if (facturas > 0) {
            conteos.add(new ConteoDependencia("facturas", "Facturas", facturas));
        }
        if (ordenes > 0) {
            conteos.add(new ConteoDependencia("ordenes", "Ordenes", ordenes));
        }
        if (cuentasPorCobrar > 0) {
            conteos.add(new ConteoDependencia("cuentasPorCobrar", "Cuentas por cobrar", cuentasPorCobrar));
        }

        boolean tieneDependencias = !conteos.isEmpty();
        return new DependenciasResponse(tieneDependencias, false, null, conteos);
    }
}
