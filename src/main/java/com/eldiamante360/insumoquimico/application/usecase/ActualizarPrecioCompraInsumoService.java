package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class ActualizarPrecioCompraInsumoService implements ActualizarPrecioCompraInsumoUseCase {

    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    public ActualizarPrecioCompraInsumoService(InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort) {
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
    }

    @Override
    public InsumoQuimicoResult ejecutar(Long insumoId, BigDecimal precioCompra) {
        InsumoQuimico insumo = insumoQuimicoRepositoryPort.buscarPorId(insumoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Insumo quimico", insumoId));

        insumo.actualizarPrecioCompra(precioCompra);

        return InsumoQuimicoAssembler.toResult(insumoQuimicoRepositoryPort.guardar(insumo));
    }
}
