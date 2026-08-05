package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ActualizarCategoriaCommand;
import com.eldiamante360.producto.application.dto.CategoriaResult;
import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActualizarCategoriaService implements ActualizarCategoriaUseCase {

    private final CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;

    public ActualizarCategoriaService(CategoriaProductoRepositoryPort categoriaProductoRepositoryPort) {
        this.categoriaProductoRepositoryPort = categoriaProductoRepositoryPort;
    }

    @Override
    public CategoriaResult ejecutar(ActualizarCategoriaCommand command) {
        CategoriaProducto categoria = categoriaProductoRepositoryPort.buscarPorId(command.categoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", command.categoriaId()));

        categoria.actualizarDatos(command.nombre(), command.descripcion());

        return ProductoAssembler.toResult(categoriaProductoRepositoryPort.guardar(categoria));
    }
}
