package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.dto.CrearClienteCommand;

public interface CrearClienteUseCase {

    ClienteResult ejecutar(CrearClienteCommand command);
}
