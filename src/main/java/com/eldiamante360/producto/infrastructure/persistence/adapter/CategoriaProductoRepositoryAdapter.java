package com.eldiamante360.producto.infrastructure.persistence.adapter;

import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.producto.infrastructure.mapper.CategoriaProductoMapper;
import com.eldiamante360.producto.infrastructure.persistence.entity.CategoriaProductoEntity;
import com.eldiamante360.producto.infrastructure.persistence.repository.CategoriaProductoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoriaProductoRepositoryAdapter implements CategoriaProductoRepositoryPort {

    private final CategoriaProductoJpaRepository categoriaProductoJpaRepository;
    private final CategoriaProductoMapper categoriaProductoMapper;

    public CategoriaProductoRepositoryAdapter(CategoriaProductoJpaRepository categoriaProductoJpaRepository,
                                               CategoriaProductoMapper categoriaProductoMapper) {
        this.categoriaProductoJpaRepository = categoriaProductoJpaRepository;
        this.categoriaProductoMapper = categoriaProductoMapper;
    }

    @Override
    public Optional<CategoriaProducto> buscarPorId(Long id) {
        return categoriaProductoJpaRepository.findById(id).map(categoriaProductoMapper::toDomain);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return categoriaProductoJpaRepository.existsByNombre(nombre);
    }

    @Override
    public CategoriaProducto guardar(CategoriaProducto categoria) {
        CategoriaProductoEntity entity = CategoriaProductoEntity.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .activo(categoria.isActivo())
                .build();

        CategoriaProductoEntity guardada = categoriaProductoJpaRepository.save(entity);
        return categoriaProductoMapper.toDomain(guardada);
    }

    @Override
    public List<CategoriaProducto> listarTodas() {
        return categoriaProductoJpaRepository.findAll().stream()
                .map(categoriaProductoMapper::toDomain)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        categoriaProductoJpaRepository.deleteById(id);
    }
}
