package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.PrecioClienteProductoRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.PrecioClienteProducto;
import com.eldiamante360.deudor.application.dto.RegistrarCreditoCommand;
import com.eldiamante360.deudor.application.usecase.RegistrarCreditoUseCase;
import com.eldiamante360.factura.application.dto.CrearFacturaCommand;
import com.eldiamante360.factura.application.dto.DetalleFacturaCommand;
import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.factura.application.port.HistorialFacturaRepositoryPort;
import com.eldiamante360.factura.domain.exception.ClienteInactivoException;
import com.eldiamante360.factura.domain.exception.DetalleFacturaVacioException;
import com.eldiamante360.factura.domain.exception.ProductoInactivoException;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Genera una factura y descuenta automaticamente el inventario de cada
 * producto facturado, dejando registro en el kardex (movimiento_inventario)
 * por cada linea. Todo ocurre en una unica transaccion: si el stock de
 * cualquier producto es insuficiente, se revierte toda la operacion (no
 * queda ni la factura ni los descuentos de stock ya aplicados).
 */
@Service
@Transactional
public class CrearFacturaService implements CrearFacturaUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final FacturaRepositoryPort facturaRepositoryPort;
    private final HistorialFacturaRepositoryPort historialFacturaRepositoryPort;
    private final MovimientoInventarioRepositoryPort movimientoInventarioRepositoryPort;
    private final RegistrarCreditoUseCase registrarCreditoUseCase;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PrecioClienteProductoRepositoryPort precioClienteProductoRepositoryPort;

    public CrearFacturaService(ClienteRepositoryPort clienteRepositoryPort,
                                ProductoRepositoryPort productoRepositoryPort,
                                FacturaRepositoryPort facturaRepositoryPort,
                                HistorialFacturaRepositoryPort historialFacturaRepositoryPort,
                                MovimientoInventarioRepositoryPort movimientoInventarioRepositoryPort,
                                RegistrarCreditoUseCase registrarCreditoUseCase,
                                UsuarioRepositoryPort usuarioRepositoryPort,
                                PrecioClienteProductoRepositoryPort precioClienteProductoRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.productoRepositoryPort = productoRepositoryPort;
        this.facturaRepositoryPort = facturaRepositoryPort;
        this.historialFacturaRepositoryPort = historialFacturaRepositoryPort;
        this.movimientoInventarioRepositoryPort = movimientoInventarioRepositoryPort;
        this.registrarCreditoUseCase = registrarCreditoUseCase;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.precioClienteProductoRepositoryPort = precioClienteProductoRepositoryPort;
    }

    @Override
    public FacturaResult ejecutar(CrearFacturaCommand command) {
        if (command.detalles() == null || command.detalles().isEmpty()) {
            throw new DetalleFacturaVacioException();
        }

        Cliente cliente = clienteRepositoryPort.buscarPorId(command.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", command.clienteId()));
        if (!cliente.isActivo()) {
            throw new ClienteInactivoException(cliente.getId());
        }

        Usuario usuario = usuarioRepositoryPort.buscarPorId(command.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", command.usuarioId()));

        String numero = facturaRepositoryPort.siguienteNumero();

        List<DetalleFactura> detalles = new ArrayList<>();
        for (DetalleFacturaCommand detalleCommand : command.detalles()) {
            Producto producto = productoRepositoryPort.buscarPorId(detalleCommand.productoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto", detalleCommand.productoId()));
            if (!producto.isActivo()) {
                throw new ProductoInactivoException(producto.getNombre());
            }

            // Si el cliente tiene un precio especial acordado para este
            // producto, se usa ese precio en vez del precio de venta
            // estandar del producto.
            Optional<PrecioClienteProducto> precioClienteProducto = precioClienteProductoRepositoryPort
                    .buscarPorClienteYProducto(cliente.getId(), producto.getId());
            BigDecimal precioUnitario = precioClienteProducto
                    .map(PrecioClienteProducto::getPrecio)
                    .orElse(producto.getPrecioVenta());

            DetalleFactura detalle = DetalleFactura.nuevo(producto.getId(), producto.getNombre(),
                    detalleCommand.cantidad(), precioUnitario, detalleCommand.porcentajeDescuento());
            detalles.add(detalle);

            // Descuento automatico del inventario: se descuenta stock y se
            // deja el movimiento en el kardex, igual que un ajuste manual.
            producto.registrarSalida(detalleCommand.cantidad());
            Producto productoActualizado = productoRepositoryPort.guardar(producto);

            movimientoInventarioRepositoryPort.guardar(MovimientoInventario.nuevo(
                    productoActualizado.getId(), TipoMovimiento.SALIDA, detalleCommand.cantidad(),
                    productoActualizado.getStockActual(), "Salida por factura " + numero, command.usuarioId()));
        }

        Factura factura = Factura.nueva(numero, cliente.getId(), cliente.getNombre(), cliente.getNumeroDocumento(),
                command.tipoPago(), detalles, command.usuarioId(), usuario.getNombreCompleto(), command.medioPago());
        Factura guardada = facturaRepositoryPort.guardar(factura);

        historialFacturaRepositoryPort.guardar(HistorialFactura.nuevo(guardada.getId(), TipoEventoFactura.CREACION,
                "Factura " + numero + " generada", command.usuarioId()));

        // Toda factura a credito genera automaticamente su cuenta por cobrar
        // en el modulo de deudores, dentro de la misma transaccion.
        if (guardada.getTipoPago() == TipoPago.CREDITO) {
            registrarCreditoUseCase.ejecutar(new RegistrarCreditoCommand(guardada.getId(), guardada.getNumero(),
                    guardada.getClienteId(), guardada.getClienteNombre(), guardada.getClienteNumeroDocumento(),
                    guardada.getTotal(), command.usuarioId()));
        }

        return FacturaAssembler.toResult(guardada);
    }
}
