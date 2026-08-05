package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.CrearInsumoQuimicoCommand;
import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.exception.NombreInsumoQuimicoDuplicadoException;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrearInsumoQuimicoService implements CrearInsumoQuimicoUseCase {

    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    public CrearInsumoQuimicoService(InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort) {
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
    }

    @Override
    public InsumoQuimicoResult ejecutar(CrearInsumoQuimicoCommand command) {
        if (insumoQuimicoRepositoryPort.existePorNombre(command.nombre())) {
            throw new NombreInsumoQuimicoDuplicadoException(command.nombre());
        }

        InsumoQuimico insumo = InsumoQuimico.nuevo(command.nombre(), command.unidadMedida(), command.precioCompra());

        return InsumoQuimicoAssembler.toResult(insumoQuimicoRepositoryPort.guardar(insumo));
    }
}
