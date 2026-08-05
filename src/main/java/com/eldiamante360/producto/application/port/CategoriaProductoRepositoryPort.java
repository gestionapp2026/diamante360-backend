package com.eldiamante360.producto.application.port;

import com.eldiamante360.producto.domain.model.CategoriaProducto;

import java.util.List;
import java.util.Optional;

public interface CategoriaProductoRepositoryPort {

    Optional<CategoriaProducto> buscarPorId(Long id);

    boolean existePorNombre(String nombre);

    CategoriaProducto guardar(CategoriaProducto categoria);

    List<CategoriaProducto> listarTodas();

    void eliminar(Long id);
}
