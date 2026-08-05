package com.eldiamante360.cliente.application.port;

import com.eldiamante360.cliente.domain.model.PrecioClienteProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PrecioClienteProductoRepositoryPort {

    PrecioClienteProducto guardar(PrecioClienteProducto precio);

    Optional<PrecioClienteProducto> buscarPorId(Long id);

    Optional<PrecioClienteProducto> buscarPorClienteYProducto(Long clienteId, Long productoId);

    Page<PrecioClienteProducto> listarPorCliente(Long clienteId, Pageable pageable);

    void eliminar(Long id);
}
