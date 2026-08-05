package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.producto.infrastructure.persistence.repository.ProductoJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarCategoriaService implements EliminarCategoriaUseCase {

    private final CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;
    private final ProductoJpaRepository productoJpaRepository;

    public EliminarCategoriaService(CategoriaProductoRepositoryPort categoriaProductoRepositoryPort,
                                     ProductoJpaRepository productoJpaRepository) {
        this.categoriaProductoRepositoryPort = categoriaProductoRepositoryPort;
        this.productoJpaRepository = productoJpaRepository;
    }

    @Override
    public void ejecutar(Long id, boolean cascada) {
        CategoriaProducto categoria = categoriaProductoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", id));

        if (productoJpaRepository.existsByCategoriaId(categoria.getId())) {
            throw new RecursoConDependenciasException("No se puede eliminar: existen productos en esta categoria.");
        }

        categoriaProductoRepositoryPort.eliminar(categoria.getId());
    }
}
