package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ProductoResult;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListarProductosStockBajoService implements ListarProductosStockBajoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;

    public ListarProductosStockBajoService(ProductoRepositoryPort productoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
    }

    @Override
    public List<ProductoResult> ejecutar() {
        return productoRepositoryPort.listarConStockBajo().stream()
                .map(ProductoAssembler::toResult)
                .toList();
    }
}
