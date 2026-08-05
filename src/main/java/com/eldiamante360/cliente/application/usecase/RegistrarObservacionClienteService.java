package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ObservacionClienteResult;
import com.eldiamante360.cliente.application.dto.RegistrarObservacionClienteCommand;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.ObservacionClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.ObservacionCliente;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegistrarObservacionClienteService implements RegistrarObservacionClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ObservacionClienteRepositoryPort observacionClienteRepositoryPort;

    public RegistrarObservacionClienteService(ClienteRepositoryPort clienteRepositoryPort,
                                               ObservacionClienteRepositoryPort observacionClienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.observacionClienteRepositoryPort = observacionClienteRepositoryPort;
    }

    @Override
    public ObservacionClienteResult ejecutar(RegistrarObservacionClienteCommand command) {
        clienteRepositoryPort.buscarPorId(command.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", command.clienteId()));

        ObservacionCliente observacion = ObservacionCliente.nueva(command.clienteId(), command.texto(), command.usuarioId());

        return ClienteAssembler.toResult(observacionClienteRepositoryPort.guardar(observacion));
    }
}
