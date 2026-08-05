package com.eldiamante360.producto.application.port;

import com.eldiamante360.producto.domain.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductoRepositoryPort {

    Optional<Producto> buscarPorId(Long id);

    boolean existePorNombre(String nombre);

    Producto guardar(Producto producto);

    Page<Producto> listar(Pageable pageable);

    List<Producto> listarConStockBajo();

    void eliminar(Long id);
}
