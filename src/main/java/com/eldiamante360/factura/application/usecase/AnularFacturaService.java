package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.deudor.application.usecase.AnularCuentaPorCobrarUseCase;
import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.factura.application.port.HistorialFacturaRepositoryPort;
import com.eldiamante360.factura.domain.model.DetalleFactura;
import com.eldiamante360.factura.domain.model.Factura;
import com.eldiamante360.factura.domain.model.HistorialFactura;
import com.eldiamante360.factura.domain.model.TipoEventoFactura;
import com.eldiamante360.factura.domain.model.TipoPago;
import com.eldiamante360.inventario.application.port.MovimientoInventarioRepositoryPort;
import com.eldiamante360.inventario.domain.model.MovimientoInventario;
import com.eldiamante360.inventario.domain.model.TipoMovimiento;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Anula una factura y revierte automaticamente el descuento de inventario
 * que se aplico al emitirla: por cada linea se registra una entrada en el
 * kardex devolviendo la cantidad facturada al stock del producto.
 */
@Service
@Transactional
public class AnularFacturaService implements AnularFacturaUseCase {

    private final FacturaRepositoryPort facturaRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final HistorialFacturaRepositoryPort historialFacturaRepositoryPort;
    private final MovimientoInventarioRepositoryPort movimientoInventarioRepositoryPort;
    private final AnularCuentaPorCobrarUseCase anularCuentaPorCobrarUseCase;

    public AnularFacturaService(FacturaRepositoryPort facturaRepositoryPort,
                                 ProductoRepositoryPort productoRepositoryPort,
                                 HistorialFacturaRepositoryPort historialFacturaRepositoryPort,
                                 MovimientoInventarioRepositoryPort movimientoInventarioRepositoryPort,
                                 AnularCuentaPorCobrarUseCase anularCuentaPorCobrarUseCase) {
        this.facturaRepositoryPort = facturaRepositoryPort;
        this.productoRepositoryPort = productoRepositoryPort;
        this.historialFacturaRepositoryPort = historialFacturaRepositoryPort;
        this.movimientoInventarioRepositoryPort = movimientoInventarioRepositoryPort;
        this.anularCuentaPorCobrarUseCase = anularCuentaPorCobrarUseCase;
    }

    @Override
    public FacturaResult ejecutar(Long facturaId, Long usuarioId) {
        Factura factura = facturaRepositoryPort.buscarPorId(facturaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Factura", facturaId));

        factura.anular();

        for (DetalleFactura detalle : factura.getDetalles()) {
            Producto producto = productoRepositoryPort.buscarPorId(detalle.productoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto", detalle.productoId()));

            producto.registrarEntrada(detalle.cantidad());
            Producto productoActualizado = productoRepositoryPort.guardar(producto);

            movimientoInventarioRepositoryPort.guardar(MovimientoInventario.nuevo(
                    productoActualizado.getId(), TipoMovimiento.ENTRADA, detalle.cantidad(),
                    productoActualizado.getStockActual(),
                    "Devolucion por anulacion de factura " + factura.getNumero(), usuarioId));
        }

        Factura guardada = facturaRepositoryPort.guardar(factura);

        historialFacturaRepositoryPort.guardar(HistorialFactura.nuevo(guardada.getId(), TipoEventoFactura.ANULACION,
                "Factura " + factura.getNumero() + " anulada", usuarioId));

        // Si la factura era a credito, la cuenta por cobrar que se genero al
        // emitirla tambien se anula, en la misma transaccion.
        if (guardada.getTipoPago() == TipoPago.CREDITO) {
            anularCuentaPorCobrarUseCase.ejecutar(guardada.getId(), usuarioId);
        }

        return FacturaAssembler.toResult(guardada);
    }
}
