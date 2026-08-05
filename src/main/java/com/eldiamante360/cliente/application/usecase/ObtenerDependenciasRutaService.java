package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.infrastructure.persistence.repository.ClienteJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ObtenerDependenciasRutaService implements ObtenerDependenciasRutaUseCase {

    private final RutaRepositoryPort rutaRepositoryPort;
    private final ClienteJpaRepository clienteJpaRepository;

    public ObtenerDependenciasRutaService(RutaRepositoryPort rutaRepositoryPort,
                                           ClienteJpaRepository clienteJpaRepository) {
        this.rutaRepositoryPort = rutaRepositoryPort;
        this.clienteJpaRepository = clienteJpaRepository;
    }

    @Override
    public DependenciasResponse ejecutar(Long id) {
        rutaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ruta", id));

        long clientesAsignados = clienteJpaRepository.countByRutaId(id);

        List<DependenciasResponse.ConteoDependencia> conteos = new ArrayList<>();
        if (clientesAsignados > 0) {
            conteos.add(new DependenciasResponse.ConteoDependencia(
                    "clientesAsignados", "Clientes asignados (se desasignaran, no se borraran)", clientesAsignados));
        }

        return new DependenciasResponse(!conteos.isEmpty(), false, null, conteos);
    }
}
