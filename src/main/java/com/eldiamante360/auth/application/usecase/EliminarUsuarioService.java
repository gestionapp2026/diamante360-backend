package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.model.Usuario;
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
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarUsuarioService implements EliminarUsuarioUseCase {

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

    public EliminarUsuarioService(UsuarioRepositoryPort usuarioRepositoryPort,
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
    public void ejecutar(Long id, Long usuarioActualId, boolean cascada) {
        if (id.equals(usuarioActualId)) {
            throw new RecursoConDependenciasException("No puedes eliminar tu propio usuario.");
        }

        Usuario usuario = usuarioRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));

        boolean tieneDependencias = facturaJpaRepository.existsByUsuarioId(usuario.getId())
                || ordenJpaRepository.existsByUsuarioId(usuario.getId())
                || movimientoInventarioJpaRepository.existsByUsuarioId(usuario.getId())
                || movimientoInsumoJpaRepository.existsByUsuarioId(usuario.getId())
                || cuentaPorCobrarJpaRepository.existsByUsuarioId(usuario.getId())
                || abonoJpaRepository.existsByUsuarioId(usuario.getId())
                || historialCuentaPorCobrarJpaRepository.existsByUsuarioId(usuario.getId())
                || historialFacturaJpaRepository.existsByUsuarioId(usuario.getId())
                || historialClienteJpaRepository.existsByUsuarioId(usuario.getId())
                || observacionClienteJpaRepository.existsByUsuarioId(usuario.getId());

        if (tieneDependencias && !cascada) {
            throw new RecursoConDependenciasException(
                    "No se puede eliminar: el usuario tiene facturas, ordenes, movimientos de kardex u otros "
                            + "registros asociados.");
        }

        if (cascada) {
            // Orden pensado para respetar FKs sin ON DELETE CASCADE (ver V7):
            // cuenta_por_cobrar.factura_id -> factura no cascadea, asi que hay
            // que vaciar primero las cuentas por cobrar generadas por las
            // facturas del usuario (lo cual a su vez cascadea via BD a sus
            // abonos/historial, V19) antes de poder borrar esas facturas.
            // Tambien se borra cualquier registro "ajeno" (historial de otro
            // cliente/factura) que el usuario haya generado, tal como lo pide
            // el negocio: al borrar un usuario no debe quedar nada que lo
            // referencie.
            cuentaPorCobrarJpaRepository.deleteByFacturaUsuarioId(usuario.getId());
            cuentaPorCobrarJpaRepository.deleteByUsuarioId(usuario.getId());
            abonoJpaRepository.deleteByUsuarioId(usuario.getId());
            historialCuentaPorCobrarJpaRepository.deleteByUsuarioId(usuario.getId());
            historialFacturaJpaRepository.deleteByUsuarioId(usuario.getId());
            facturaJpaRepository.deleteByUsuarioId(usuario.getId());
            ordenJpaRepository.deleteByUsuarioId(usuario.getId());
            movimientoInventarioJpaRepository.deleteByUsuarioId(usuario.getId());
            movimientoInsumoJpaRepository.deleteByUsuarioId(usuario.getId());
            historialClienteJpaRepository.deleteByUsuarioId(usuario.getId());
            observacionClienteJpaRepository.deleteByUsuarioId(usuario.getId());
        }

        usuarioRepositoryPort.eliminar(usuario.getId());
    }
}
