package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.RutaResult;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListarRutasService implements ListarRutasUseCase {

    private final RutaRepositoryPort rutaRepositoryPort;

    public ListarRutasService(RutaRepositoryPort rutaRepositoryPort) {
        this.rutaRepositoryPort = rutaRepositoryPort;
    }

    @Override
    public List<RutaResult> ejecutar() {
        return rutaRepositoryPort.listarTodas().stream()
                .map(ClienteAssembler::toResult)
                .toList();
    }
}
