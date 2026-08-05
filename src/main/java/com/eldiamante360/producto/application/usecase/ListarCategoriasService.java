package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.CategoriaResult;
import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListarCategoriasService implements ListarCategoriasUseCase {

    private final CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;

    public ListarCategoriasService(CategoriaProductoRepositoryPort categoriaProductoRepositoryPort) {
        this.categoriaProductoRepositoryPort = categoriaProductoRepositoryPort;
    }

    @Override
    public List<CategoriaResult> ejecutar() {
        return categoriaProductoRepositoryPort.listarTodas().stream()
                .map(ProductoAssembler::toResult)
                .toList();
    }
}
