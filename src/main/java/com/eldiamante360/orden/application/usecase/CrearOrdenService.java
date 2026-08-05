package com.eldiamante360.orden.application.usecase;

import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.orden.application.dto.CrearOrdenCommand;
import com.eldiamante360.orden.application.dto.DetalleOrdenCommand;
import com.eldiamante360.orden.application.dto.OrdenResult;
import com.eldiamante360.orden.application.port.OrdenRepositoryPort;
import com.eldiamante360.orden.domain.exception.ClienteInactivoException;
import com.eldiamante360.orden.domain.exception.DetalleOrdenVacioException;
import com.eldiamante360.orden.domain.exception.ProductoInactivoException;
import com.eldiamante360.orden.domain.model.DetalleOrden;
import com.eldiamante360.orden.domain.model.Orden;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Crea una orden (pedido anticipado). A diferencia de CrearFacturaService,
 * esto NO toca inventario/stock: la orden es solo un seguimiento de
 * intencion de venta, el descuento de stock ocurre unicamente cuando se
 * genera la factura real (fuera del alcance de este modulo).
 */
@Service
@Transactional
public class CrearOrdenService implements CrearOrdenUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final OrdenRepositoryPort ordenRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public CrearOrdenService(ClienteRepositoryPort clienteRepositoryPort,
                              ProductoRepositoryPort productoRepositoryPort,
                              OrdenRepositoryPort ordenRepositoryPort,
                              UsuarioRepositoryPort usuarioRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.productoRepositoryPort = productoRepositoryPort;
        this.ordenRepositoryPort = ordenRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public OrdenResult ejecutar(CrearOrdenCommand command) {
        if (command.detalles() == null || command.detalles().isEmpty()) {
            throw new DetalleOrdenVacioException();
        }

        Cliente cliente = clienteRepositoryPort.buscarPorId(command.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", command.clienteId()));
        if (!cliente.isActivo()) {
            throw new ClienteInactivoException(cliente.getId());
        }

        Usuario usuario = usuarioRepositoryPort.buscarPorId(command.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", command.usuarioId()));

        String numero = ordenRepositoryPort.siguienteNumero();

        List<DetalleOrden> detalles = new ArrayList<>();
        for (DetalleOrdenCommand detalleCommand : command.detalles()) {
            Producto producto = productoRepositoryPort.buscarPorId(detalleCommand.productoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto", detalleCommand.productoId()));
            if (!producto.isActivo()) {
                throw new ProductoInactivoException(producto.getNombre());
            }

            detalles.add(DetalleOrden.nuevo(producto.getId(), producto.getNombre(), detalleCommand.cantidad()));
        }

        Orden orden = Orden.nueva(numero, cliente.getId(), cliente.getNombre(), command.fechaEntrega(),
                command.observaciones(), detalles, command.usuarioId(), usuario.getNombreCompleto());
        Orden guardada = ordenRepositoryPort.guardar(orden);

        return OrdenAssembler.toResult(guardada);
    }
}
