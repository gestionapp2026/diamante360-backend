package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.RutaResult;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.domain.model.Ruta;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CambiarEstadoRutaService implements CambiarEstadoRutaUseCase {

    private final RutaRepositoryPort rutaRepositoryPort;

    public CambiarEstadoRutaService(RutaRepositoryPort rutaRepositoryPort) {
        this.rutaRepositoryPort = rutaRepositoryPort;
    }

    @Override
    public RutaResult ejecutar(Long rutaId, boolean activo) {
        Ruta ruta = rutaRepositoryPort.buscarPorId(rutaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ruta", rutaId));

        if (activo) {
            ruta.activar();
        } else {
            ruta.desactivar();
        }

        return ClienteAssembler.toResult(rutaRepositoryPort.guardar(ruta));
    }
}
