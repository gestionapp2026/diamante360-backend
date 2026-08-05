package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ClienteResult;

public interface CambiarEstadoClienteUseCase {

    ClienteResult ejecutar(Long clienteId, boolean activo, Long usuarioId);
}
