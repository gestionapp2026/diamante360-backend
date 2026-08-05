package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ProductoResult;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarProductosService implements ListarProductosUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;

    public ListarProductosService(ProductoRepositoryPort productoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
    }

    @Override
    public Page<ProductoResult> ejecutar(Pageable pageable) {
        return productoRepositoryPort.listar(pageable).map(ProductoAssembler::toResult);
    }
}
