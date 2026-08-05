package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.PrecioClienteProductoResult;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.PrecioClienteProductoRepositoryPort;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarPreciosClienteService implements ListarPreciosClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final PrecioClienteProductoRepositoryPort precioClienteProductoRepositoryPort;

    public ListarPreciosClienteService(ClienteRepositoryPort clienteRepositoryPort,
                                        ProductoRepositoryPort productoRepositoryPort,
                                        PrecioClienteProductoRepositoryPort precioClienteProductoRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.productoRepositoryPort = productoRepositoryPort;
        this.precioClienteProductoRepositoryPort = precioClienteProductoRepositoryPort;
    }

    @Override
    public Page<PrecioClienteProductoResult> ejecutar(Long clienteId, Pageable pageable) {
        clienteRepositoryPort.buscarPorId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", clienteId));

        return precioClienteProductoRepositoryPort.listarPorCliente(clienteId, pageable)
                .map(precio -> {
                    String productoNombre = productoRepositoryPort.buscarPorId(precio.getProductoId())
                            .map(producto -> producto.getNombre())
                            .orElse(null);
                    return ClienteAssembler.toResult(precio, productoNombre);
                });
    }
}
