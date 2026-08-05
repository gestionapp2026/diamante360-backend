package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ObservacionClienteResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarObservacionesClienteUseCase {

    Page<ObservacionClienteResult> ejecutar(Long clienteId, Pageable pageable);
}
