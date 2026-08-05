package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ClienteResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarClientesUseCase {

    Page<ClienteResult> ejecutar(Pageable pageable);
}
