package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ActualizarClienteCommand;
import com.eldiamante360.cliente.application.dto.ClienteResult;

public interface ActualizarClienteUseCase {

    ClienteResult ejecutar(ActualizarClienteCommand command);
}
