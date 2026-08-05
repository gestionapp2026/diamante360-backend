package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ClienteResult;

public interface ObtenerClienteUseCase {

    ClienteResult ejecutar(Long clienteId);
}
