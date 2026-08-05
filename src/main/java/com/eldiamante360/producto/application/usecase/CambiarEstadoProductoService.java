package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ProductoResult;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CambiarEstadoProductoService implements CambiarEstadoProductoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;

    public CambiarEstadoProductoService(ProductoRepositoryPort productoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
    }

    @Override
    public ProductoResult ejecutar(Long productoId, boolean activo) {
        Producto producto = productoRepositoryPort.buscarPorId(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", productoId));

        if (activo) {
            producto.activar();
        } else {
            producto.desactivar();
        }

        return ProductoAssembler.toResult(productoRepositoryPort.guardar(producto));
    }
}
