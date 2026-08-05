package com.eldiamante360.inventario.application.port;

import com.eldiamante360.inventario.domain.model.MovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovimientoInventarioRepositoryPort {

    MovimientoInventario guardar(MovimientoInventario movimiento);

    Page<MovimientoInventario> listarPorProducto(Long productoId, Pageable pageable);
}
