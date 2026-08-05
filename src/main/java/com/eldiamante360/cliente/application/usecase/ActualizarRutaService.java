package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ActualizarRutaCommand;
import com.eldiamante360.cliente.application.dto.RutaResult;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.domain.model.Ruta;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActualizarRutaService implements ActualizarRutaUseCase {

    private final RutaRepositoryPort rutaRepositoryPort;

    public ActualizarRutaService(RutaRepositoryPort rutaRepositoryPort) {
        this.rutaRepositoryPort = rutaRepositoryPort;
    }

    @Override
    public RutaResult ejecutar(ActualizarRutaCommand command) {
        Ruta ruta = rutaRepositoryPort.buscarPorId(command.rutaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ruta", command.rutaId()));

        ruta.actualizarDatos(command.nombre(), command.descripcion());

        return ClienteAssembler.toResult(rutaRepositoryPort.guardar(ruta));
    }
}
