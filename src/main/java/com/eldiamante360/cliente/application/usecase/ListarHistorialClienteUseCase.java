package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.HistorialClienteResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarHistorialClienteUseCase {

    Page<HistorialClienteResult> ejecutar(Long clienteId, Pageable pageable);
}
