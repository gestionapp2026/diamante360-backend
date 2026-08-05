package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.AsignarRutaClienteCommand;
import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.HistorialClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
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
public class AsignarRutaClienteService implements AsignarRutaClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final RutaRepositoryPort rutaRepositoryPort;
    private final HistorialClienteRepositoryPort historialClienteRepositoryPort;

    public AsignarRutaClienteService(ClienteRepositoryPort clienteRepositoryPort,
                                      RutaRepositoryPort rutaRepositoryPort,
                                      HistorialClienteRepositoryPort historialClienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.rutaRepositoryPort = rutaRepositoryPort;
        this.historialClienteRepositoryPort = historialClienteRepositoryPort;
    }

    @Override
    public ClienteResult ejecutar(AsignarRutaClienteCommand command) {
        Cliente cliente = clienteRepositoryPort.buscarPorId(command.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", command.clienteId()));

        String descripcion;
        if (command.rutaId() != null) {
            Ruta ruta = rutaRepositoryPort.buscarPorId(command.rutaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Ruta", command.rutaId()));
            if (!ruta.isActivo()) {
                throw new RutaInactivaException(ruta.getNombre());
            }
            cliente.asignarRuta(ruta);
            descripcion = "Ruta asignada: '%s'".formatted(ruta.getNombre());
        } else {
            cliente.asignarRuta(null);
            descripcion = "Ruta removida del cliente";
        }

        Cliente guardado = clienteRepositoryPort.guardar(cliente);

        historialClienteRepositoryPort.guardar(HistorialCliente.nuevo(guardado.getId(), TipoEventoCliente.CAMBIO_RUTA,
                descripcion, command.usuarioId()));

        return ClienteAssembler.toResult(guardado);
    }
}
