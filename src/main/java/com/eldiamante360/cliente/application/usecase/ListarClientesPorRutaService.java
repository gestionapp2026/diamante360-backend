package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarClientesPorRutaService implements ListarClientesPorRutaUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final RutaRepositoryPort rutaRepositoryPort;

    public ListarClientesPorRutaService(ClienteRepositoryPort clienteRepositoryPort,
                                         RutaRepositoryPort rutaRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.rutaRepositoryPort = rutaRepositoryPort;
    }

    @Override
    public Page<ClienteResult> ejecutar(Long rutaId, Pageable pageable) {
        rutaRepositoryPort.buscarPorId(rutaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ruta", rutaId));

        return clienteRepositoryPort.listarPorRuta(rutaId, pageable).map(ClienteAssembler::toResult);
    }
}
