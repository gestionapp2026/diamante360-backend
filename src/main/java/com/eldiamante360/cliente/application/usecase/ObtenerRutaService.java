package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.RutaResult;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.domain.model.Ruta;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObtenerRutaService implements ObtenerRutaUseCase {

    private final RutaRepositoryPort rutaRepositoryPort;

    public ObtenerRutaService(RutaRepositoryPort rutaRepositoryPort) {
        this.rutaRepositoryPort = rutaRepositoryPort;
    }

    @Override
    public RutaResult ejecutar(Long rutaId) {
        Ruta ruta = rutaRepositoryPort.buscarPorId(rutaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ruta", rutaId));

        return ClienteAssembler.toResult(ruta);
    }
}
