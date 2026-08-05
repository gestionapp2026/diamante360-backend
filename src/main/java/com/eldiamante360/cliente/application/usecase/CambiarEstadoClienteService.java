package com.eldiamante360.cliente.application.usecase;

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
public class CambiarEstadoClienteService implements CambiarEstadoClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final HistorialClienteRepositoryPort historialClienteRepositoryPort;

    public CambiarEstadoClienteService(ClienteRepositoryPort clienteRepositoryPort,
                                        HistorialClienteRepositoryPort historialClienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.historialClienteRepositoryPort = historialClienteRepositoryPort;
    }

    @Override
    public ClienteResult ejecutar(Long clienteId, boolean activo, Long usuarioId) {
        Cliente cliente = clienteRepositoryPort.buscarPorId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", clienteId));

        TipoEventoCliente tipoEvento;
        String descripcion;
        if (activo) {
            cliente.activar();
            tipoEvento = TipoEventoCliente.ACTIVACION;
            descripcion = "Cliente activado";
        } else {
            cliente.desactivar();
            tipoEvento = TipoEventoCliente.DESACTIVACION;
            descripcion = "Cliente desactivado";
        }

        Cliente guardado = clienteRepositoryPort.guardar(cliente);

        historialClienteRepositoryPort.guardar(HistorialCliente.nuevo(guardado.getId(), tipoEvento, descripcion, usuarioId));

        return ClienteAssembler.toResult(guardado);
    }
}
