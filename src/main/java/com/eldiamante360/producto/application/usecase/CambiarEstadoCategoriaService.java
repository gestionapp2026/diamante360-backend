package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.CategoriaResult;
import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CambiarEstadoCategoriaService implements CambiarEstadoCategoriaUseCase {

    private final CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;

    public CambiarEstadoCategoriaService(CategoriaProductoRepositoryPort categoriaProductoRepositoryPort) {
        this.categoriaProductoRepositoryPort = categoriaProductoRepositoryPort;
    }

    @Override
    public CategoriaResult ejecutar(Long categoriaId, boolean activo) {
        CategoriaProducto categoria = categoriaProductoRepositoryPort.buscarPorId(categoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", categoriaId));

        if (activo) {
            categoria.activar();
        } else {
            categoria.desactivar();
        }

        return ProductoAssembler.toResult(categoriaProductoRepositoryPort.guardar(categoria));
    }
}
