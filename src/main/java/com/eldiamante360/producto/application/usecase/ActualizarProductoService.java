package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ActualizarProductoCommand;
import com.eldiamante360.producto.application.dto.ProductoResult;
import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.exception.CategoriaInactivaException;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActualizarProductoService implements ActualizarProductoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;
    private final CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;

    public ActualizarProductoService(ProductoRepositoryPort productoRepositoryPort,
                                      CategoriaProductoRepositoryPort categoriaProductoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
        this.categoriaProductoRepositoryPort = categoriaProductoRepositoryPort;
    }

    @Override
    public ProductoResult ejecutar(ActualizarProductoCommand command) {
        Producto producto = productoRepositoryPort.buscarPorId(command.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", command.productoId()));

        CategoriaProducto categoria = categoriaProductoRepositoryPort.buscarPorId(command.categoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", command.categoriaId()));

        if (!categoria.isActivo()) {
            throw new CategoriaInactivaException(categoria.getNombre());
        }

        producto.actualizarDatos(command.nombre(), categoria, command.precioCompra(), command.precioVenta(),
                command.stockMinimo());

        return ProductoAssembler.toResult(productoRepositoryPort.guardar(producto));
    }
}
