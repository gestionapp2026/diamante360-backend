package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.CategoriaResult;
import com.eldiamante360.producto.application.dto.CrearCategoriaCommand;
import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.domain.exception.NombreCategoriaDuplicadaException;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrearCategoriaService implements CrearCategoriaUseCase {

    private final CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;

    public CrearCategoriaService(CategoriaProductoRepositoryPort categoriaProductoRepositoryPort) {
        this.categoriaProductoRepositoryPort = categoriaProductoRepositoryPort;
    }

    @Override
    public CategoriaResult ejecutar(CrearCategoriaCommand command) {
        if (categoriaProductoRepositoryPort.existePorNombre(command.nombre())) {
            throw new NombreCategoriaDuplicadaException(command.nombre());
        }

        CategoriaProducto categoria = CategoriaProducto.nueva(command.nombre(), command.descripcion());
        return ProductoAssembler.toResult(categoriaProductoRepositoryPort.guardar(categoria));
    }
}
