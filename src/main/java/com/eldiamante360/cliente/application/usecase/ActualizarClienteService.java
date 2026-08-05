package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ActualizarClienteCommand;
import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.HistorialClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.HistorialCliente;
import com.eldiamante360.cliente.domain.model.TipoEventoCliente;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActualizarClienteService implements ActualizarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final HistorialClienteRepositoryPort historialClienteRepositoryPort;

    public ActualizarClienteService(ClienteRepositoryPort clienteRepositoryPort,
                                     HistorialClienteRepositoryPort historialClienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.historialClienteRepositoryPort = historialClienteRepositoryPort;
    }

    @Override
    public ClienteResult ejecutar(ActualizarClienteCommand command) {
        Cliente cliente = clienteRepositoryPort.buscarPorId(command.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", command.clienteId()));

        cliente.actualizarDatos(command.nombre(), command.telefono(), command.email(), command.direccion());

        Cliente guardado = clienteRepositoryPort.guardar(cliente);

        historialClienteRepositoryPort.guardar(HistorialCliente.nuevo(guardado.getId(), TipoEventoCliente.EDICION,
                "Datos del cliente actualizados", command.usuarioId()));

        return ClienteAssembler.toResult(guardado);
    }
}
