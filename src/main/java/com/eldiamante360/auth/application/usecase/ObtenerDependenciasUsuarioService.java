package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.cliente.infrastructure.persistence.repository.HistorialClienteJpaRepository;
import com.eldiamante360.cliente.infrastructure.persistence.repository.ObservacionClienteJpaRepository;
import com.eldiamante360.deudor.infrastructure.persistence.repository.AbonoJpaRepository;
import com.eldiamante360.deudor.infrastructure.persistence.repository.CuentaPorCobrarJpaRepository;
import com.eldiamante360.deudor.infrastructure.persistence.repository.HistorialCuentaPorCobrarJpaRepository;
import com.eldiamante360.factura.infrastructure.persistence.repository.FacturaJpaRepository;
import com.eldiamante360.factura.infrastructure.persistence.repository.HistorialFacturaJpaRepository;
import com.eldiamante360.insumoquimico.infrastructure.persistence.repository.MovimientoInsumoJpaRepository;
import com.eldiamante360.inventario.infrastructure.persistence.repository.MovimientoInventarioJpaRepository;
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
public class ObtenerDependenciasUsuarioService implements ObtenerDependenciasUsuarioUseCase {

    static final String MENSAJE_BLOQUEO_AUTOELIMINACION = "No puedes eliminar tu propio usuario.";

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final FacturaJpaRepository facturaJpaRepository;
    private final OrdenJpaRepository ordenJpaRepository;
    private final MovimientoInventarioJpaRepository movimientoInventarioJpaRepository;
    private final MovimientoInsumoJpaRepository movimientoInsumoJpaRepository;
    private final CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository;
    private final AbonoJpaRepository abonoJpaRepository;
    private final HistorialCuentaPorCobrarJpaRepository historialCuentaPorCobrarJpaRepository;
    private final HistorialFacturaJpaRepository historialFacturaJpaRepository;
    private final HistorialClienteJpaRepository historialClienteJpaRepository;
    private final ObservacionClienteJpaRepository observacionClienteJpaRepository;

    public ObtenerDependenciasUsuarioService(UsuarioRepositoryPort usuarioRepositoryPort,
                                              FacturaJpaRepository facturaJpaRepository,
                                              OrdenJpaRepository ordenJpaRepository,
                                              MovimientoInventarioJpaRepository movimientoInventarioJpaRepository,
                                              MovimientoInsumoJpaRepository movimientoInsumoJpaRepository,
                                              CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository,
                                              AbonoJpaRepository abonoJpaRepository,
                                              HistorialCuentaPorCobrarJpaRepository historialCuentaPorCobrarJpaRepository,
                                              HistorialFacturaJpaRepository historialFacturaJpaRepository,
                                              HistorialClienteJpaRepository historialClienteJpaRepository,
                                              ObservacionClienteJpaRepository observacionClienteJpaRepository) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.facturaJpaRepository = facturaJpaRepository;
        this.ordenJpaRepository = ordenJpaRepository;
        this.movimientoInventarioJpaRepository = movimientoInventarioJpaRepository;
        this.movimientoInsumoJpaRepository = movimientoInsumoJpaRepository;
        this.cuentaPorCobrarJpaRepository = cuentaPorCobrarJpaRepository;
        this.abonoJpaRepository = abonoJpaRepository;
        this.historialCuentaPorCobrarJpaRepository = historialCuentaPorCobrarJpaRepository;
        this.historialFacturaJpaRepository = historialFacturaJpaRepository;
        this.historialClienteJpaRepository = historialClienteJpaRepository;
        this.observacionClienteJpaRepository = observacionClienteJpaRepository;
    }

    @Override
    public DependenciasResponse ejecutar(Long id, Long usuarioActualId) {
        if (id.equals(usuarioActualId)) {
            return new DependenciasResponse(false, true, MENSAJE_BLOQUEO_AUTOELIMINACION, List.of());
        }

        var usuario = usuarioRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));

        long facturas = facturaJpaRepository.countByUsuarioId(usuario.getId());
        long ordenes = ordenJpaRepository.countByUsuarioId(usuario.getId());
        long movimientosInventario = movimientoInventarioJpaRepository.countByUsuarioId(usuario.getId());
        long movimientosInsumo = movimientoInsumoJpaRepository.countByUsuarioId(usuario.getId());
        long cuentasPorCobrar = cuentaPorCobrarJpaRepository.countByUsuarioId(usuario.getId());
        long abonos = abonoJpaRepository.countByUsuarioId(usuario.getId());
        long historialCuentasPorCobrar = historialCuentaPorCobrarJpaRepository.countByUsuarioId(usuario.getId());
        long historialFacturas = historialFacturaJpaRepository.countByUsuarioId(usuario.getId());
        long historialClientes = historialClienteJpaRepository.countByUsuarioId(usuario.getId());
        long observacionesCliente = observacionClienteJpaRepository.countByUsuarioId(usuario.getId());

        List<ConteoDependencia> conteos = new ArrayList<>();
        if (facturas > 0) {
            conteos.add(new ConteoDependencia("facturas", "Facturas", facturas));
        }
        if (ordenes > 0) {
            conteos.add(new ConteoDependencia("ordenes", "Ordenes", ordenes));
        }
        if (movimientosInventario > 0) {
            conteos.add(new ConteoDependencia("movimientosInventario", "Movimientos de inventario", movimientosInventario));
        }
        if (movimientosInsumo > 0) {
            conteos.add(new ConteoDependencia("movimientosInsumo", "Movimientos de insumo", movimientosInsumo));
        }
        if (cuentasPorCobrar > 0) {
            conteos.add(new ConteoDependencia("cuentasPorCobrar", "Cuentas por cobrar", cuentasPorCobrar));
        }
        if (abonos > 0) {
            conteos.add(new ConteoDependencia("abonos", "Abonos", abonos));
        }
        if (historialCuentasPorCobrar > 0) {
            conteos.add(new ConteoDependencia("historialCuentasPorCobrar", "Historial de cuentas por cobrar",
                    historialCuentasPorCobrar));
        }
        if (historialFacturas > 0) {
            conteos.add(new ConteoDependencia("historialFacturas", "Historial de facturas", historialFacturas));
        }
        if (historialClientes > 0) {
            conteos.add(new ConteoDependencia("historialClientes", "Historial de clientes", historialClientes));
        }
        if (observacionesCliente > 0) {
            conteos.add(new ConteoDependencia("observacionesCliente", "Observaciones de clientes", observacionesCliente));
        }

        boolean tieneDependencias = !conteos.isEmpty();

        // A diferencia de otros modulos, el usuario nunca queda bloqueado por
        // dependencias: si tiene registros asociados, se cascadean junto con
        // el usuario al confirmar el borrado.
        return new DependenciasResponse(tieneDependencias, false, null, conteos);
    }
}
