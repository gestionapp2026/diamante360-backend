package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ObservacionClienteResult;
import com.eldiamante360.cliente.application.dto.RegistrarObservacionClienteCommand;

public interface RegistrarObservacionClienteUseCase {

    ObservacionClienteResult ejecutar(RegistrarObservacionClienteCommand command);
}
