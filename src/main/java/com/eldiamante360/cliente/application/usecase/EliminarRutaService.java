package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.infrastructure.persistence.repository.ClienteJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarRutaService implements EliminarRutaUseCase {

    private final RutaRepositoryPort rutaRepositoryPort;
    private final ClienteJpaRepository clienteJpaRepository;

    public EliminarRutaService(RutaRepositoryPort rutaRepositoryPort, ClienteJpaRepository clienteJpaRepository) {
        this.rutaRepositoryPort = rutaRepositoryPort;
        this.clienteJpaRepository = clienteJpaRepository;
    }

    @Override
    public void ejecutar(Long id, boolean cascada) {
        rutaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ruta", id));

        clienteJpaRepository.desasignarRuta(id);

        rutaRepositoryPort.eliminar(id);
    }
}
