package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.dto.CrearClienteCommand;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.HistorialClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.domain.exception.NumeroDocumentoDuplicadoException;
import com.eldiamante360.cliente.domain.exception.RutaInactivaException;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.HistorialCliente;
import com.eldiamante360.cliente.domain.model.Ruta;
import com.eldiamante360.cliente.domain.model.TipoEventoCliente;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrearClienteService implements CrearClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final RutaRepositoryPort rutaRepositoryPort;
    private final HistorialClienteRepositoryPort historialClienteRepositoryPort;

    public CrearClienteService(ClienteRepositoryPort clienteRepositoryPort,
                                RutaRepositoryPort rutaRepositoryPort,
                                HistorialClienteRepositoryPort historialClienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.rutaRepositoryPort = rutaRepositoryPort;
        this.historialClienteRepositoryPort = historialClienteRepositoryPort;
    }

    @Override
    public ClienteResult ejecutar(CrearClienteCommand command) {
        if (clienteRepositoryPort.existePorNumeroDocumento(command.numeroDocumento())) {
            throw new NumeroDocumentoDuplicadoException(command.numeroDocumento());
        }

        Ruta ruta = null;
        if (command.rutaId() != null) {
            ruta = rutaRepositoryPort.buscarPorId(command.rutaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Ruta", command.rutaId()));
            if (!ruta.isActivo()) {
                throw new RutaInactivaException(ruta.getNombre());
            }
        }

        Cliente cliente = Cliente.nuevo(command.tipoDocumento(), command.numeroDocumento(), command.nombre(),
                command.telefono(), command.email(), command.direccion(), ruta);

        Cliente guardado = clienteRepositoryPort.guardar(cliente);

        historialClienteRepositoryPort.guardar(HistorialCliente.nuevo(guardado.getId(), TipoEventoCliente.CREACION,
                "Cliente creado", command.usuarioId()));

        return ClienteAssembler.toResult(guardado);
    }
}
