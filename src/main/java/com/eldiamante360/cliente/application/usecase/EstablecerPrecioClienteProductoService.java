package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.EstablecerPrecioClienteProductoCommand;
import com.eldiamante360.cliente.application.dto.PrecioClienteProductoResult;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.PrecioClienteProductoRepositoryPort;
import com.eldiamante360.cliente.domain.model.PrecioClienteProducto;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Establece (crea o actualiza) el precio especial de un producto para un
 * cliente. Es un upsert: si ya existe un registro para el par
 * (clienteId, productoId) se actualiza su precio, de lo contrario se crea
 * uno nuevo.
 */
@Service
@Transactional
public class EstablecerPrecioClienteProductoService implements EstablecerPrecioClienteProductoUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final PrecioClienteProductoRepositoryPort precioClienteProductoRepositoryPort;

    public EstablecerPrecioClienteProductoService(ClienteRepositoryPort clienteRepositoryPort,
                                                    ProductoRepositoryPort productoRepositoryPort,
                                                    PrecioClienteProductoRepositoryPort precioClienteProductoRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.productoRepositoryPort = productoRepositoryPort;
        this.precioClienteProductoRepositoryPort = precioClienteProductoRepositoryPort;
    }

    @Override
    public PrecioClienteProductoResult ejecutar(EstablecerPrecioClienteProductoCommand command) {
        clienteRepositoryPort.buscarPorId(command.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", command.clienteId()));

        Producto producto = productoRepositoryPort.buscarPorId(command.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", command.productoId()));

        PrecioClienteProducto precio = precioClienteProductoRepositoryPort
                .buscarPorClienteYProducto(command.clienteId(), command.productoId())
                .map(existente -> {
                    existente.actualizarPrecio(command.precio());
                    return existente;
                })
                .orElseGet(() -> PrecioClienteProducto.nuevo(command.clienteId(), command.productoId(), command.precio()));

        PrecioClienteProducto guardado = precioClienteProductoRepositoryPort.guardar(precio);
        return ClienteAssembler.toResult(guardado, producto.getNombre());
    }
}
