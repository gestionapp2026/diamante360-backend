package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.CrearProductoCommand;
import com.eldiamante360.producto.application.dto.ProductoResult;
import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.exception.CategoriaInactivaException;
import com.eldiamante360.producto.domain.exception.NombreProductoDuplicadoException;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrearProductoService implements CrearProductoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;
    private final CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;

    public CrearProductoService(ProductoRepositoryPort productoRepositoryPort,
                                 CategoriaProductoRepositoryPort categoriaProductoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
        this.categoriaProductoRepositoryPort = categoriaProductoRepositoryPort;
    }

    @Override
    public ProductoResult ejecutar(CrearProductoCommand command) {
        if (productoRepositoryPort.existePorNombre(command.nombre())) {
            throw new NombreProductoDuplicadoException(command.nombre());
        }

        CategoriaProducto categoria = categoriaProductoRepositoryPort.buscarPorId(command.categoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", command.categoriaId()));

        if (!categoria.isActivo()) {
            throw new CategoriaInactivaException(categoria.getNombre());
        }

        Producto producto = Producto.nuevo(command.nombre(), categoria, command.tipoVenta(), command.unidadMedida(),
                command.precioCompra(), command.precioVenta(), command.stockInicial(), command.stockMinimo());

        return ProductoAssembler.toResult(productoRepositoryPort.guardar(producto));
    }
}
