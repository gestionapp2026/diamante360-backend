package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.CrearRutaCommand;
import com.eldiamante360.cliente.application.dto.RutaResult;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.domain.exception.NombreRutaDuplicadaException;
import com.eldiamante360.cliente.domain.model.Ruta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrearRutaService implements CrearRutaUseCase {

    private final RutaRepositoryPort rutaRepositoryPort;

    public CrearRutaService(RutaRepositoryPort rutaRepositoryPort) {
        this.rutaRepositoryPort = rutaRepositoryPort;
    }

    @Override
    public RutaResult ejecutar(CrearRutaCommand command) {
        if (rutaRepositoryPort.existePorNombre(command.nombre())) {
            throw new NombreRutaDuplicadaException(command.nombre());
        }

        Ruta ruta = Ruta.nueva(command.nombre(), command.descripcion());
        return ClienteAssembler.toResult(rutaRepositoryPort.guardar(ruta));
    }
}
