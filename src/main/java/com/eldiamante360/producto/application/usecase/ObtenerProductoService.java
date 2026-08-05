package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ProductoResult;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObtenerProductoService implements ObtenerProductoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;

    public ObtenerProductoService(ProductoRepositoryPort productoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
    }

    @Override
    public ProductoResult ejecutar(Long productoId) {
        return productoRepositoryPort.buscarPorId(productoId)
                .map(ProductoAssembler::toResult)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", productoId));
    }
}
