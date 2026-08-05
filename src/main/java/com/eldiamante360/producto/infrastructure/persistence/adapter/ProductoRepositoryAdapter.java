package com.eldiamante360.producto.infrastructure.persistence.adapter;

import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.producto.infrastructure.mapper.ProductoMapper;
import com.eldiamante360.producto.infrastructure.persistence.entity.CategoriaProductoEntity;
import com.eldiamante360.producto.infrastructure.persistence.entity.ProductoEntity;
import com.eldiamante360.producto.infrastructure.persistence.repository.CategoriaProductoJpaRepository;
import com.eldiamante360.producto.infrastructure.persistence.repository.ProductoJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final ProductoJpaRepository productoJpaRepository;
    private final CategoriaProductoJpaRepository categoriaProductoJpaRepository;
    private final ProductoMapper productoMapper;

    public ProductoRepositoryAdapter(ProductoJpaRepository productoJpaRepository,
                                      CategoriaProductoJpaRepository categoriaProductoJpaRepository,
                                      ProductoMapper productoMapper) {
        this.productoJpaRepository = productoJpaRepository;
        this.categoriaProductoJpaRepository = categoriaProductoJpaRepository;
        this.productoMapper = productoMapper;
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return productoJpaRepository.findById(id).map(productoMapper::toDomain);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return productoJpaRepository.existsByNombre(nombre);
    }

    @Override
    public Producto guardar(Producto producto) {
        CategoriaProductoEntity categoriaReferencia =
                categoriaProductoJpaRepository.getReferenceById(producto.getCategoria().getId());

        ProductoEntity entity = ProductoEntity.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .categoria(categoriaReferencia)
                .tipoVenta(producto.getTipoVenta())
                .unidadMedida(producto.getUnidadMedida())
                .precioCompra(producto.getPrecioCompra())
                .precioVenta(producto.getPrecioVenta())
                .stockActual(producto.getStockActual())
                .stockMinimo(producto.getStockMinimo())
                .activo(producto.isActivo())
                .version(producto.getVersion())
                .build();

        ProductoEntity guardado = productoJpaRepository.save(entity);
        return productoMapper.toDomain(guardado);
    }

    @Override
    public Page<Producto> listar(Pageable pageable) {
        return productoJpaRepository.findAll(pageable).map(productoMapper::toDomain);
    }

    @Override
    public List<Producto> listarConStockBajo() {
        return productoJpaRepository.findConStockBajo().stream()
                .map(productoMapper::toDomain)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        productoJpaRepository.deleteById(id);
    }
}
