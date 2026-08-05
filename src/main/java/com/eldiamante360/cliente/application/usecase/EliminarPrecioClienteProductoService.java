package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.port.PrecioClienteProductoRepositoryPort;
import com.eldiamante360.cliente.domain.model.PrecioClienteProducto;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarPrecioClienteProductoService implements EliminarPrecioClienteProductoUseCase {

    private final PrecioClienteProductoRepositoryPort precioClienteProductoRepositoryPort;

    public EliminarPrecioClienteProductoService(PrecioClienteProductoRepositoryPort precioClienteProductoRepositoryPort) {
        this.precioClienteProductoRepositoryPort = precioClienteProductoRepositoryPort;
    }

    @Override
    public void ejecutar(Long clienteId, Long precioId) {
        PrecioClienteProducto precio = precioClienteProductoRepositoryPort.buscarPorId(precioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Precio cliente producto", precioId));

        if (!precio.getClienteId().equals(clienteId)) {
            throw new RecursoNoEncontradoException("Precio cliente producto", precioId);
        }

        precioClienteProductoRepositoryPort.eliminar(precioId);
    }
}
