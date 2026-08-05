package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.AsignarRutaClienteCommand;
import com.eldiamante360.cliente.application.dto.ClienteResult;

public interface AsignarRutaClienteUseCase {

    ClienteResult ejecutar(AsignarRutaClienteCommand command);
}
