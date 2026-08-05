package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.PrecioClienteProductoResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarPreciosClienteUseCase {

    Page<PrecioClienteProductoResult> ejecutar(Long clienteId, Pageable pageable);
}
