package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.MovimientoInsumoResult;
import com.eldiamante360.insumoquimico.application.dto.RegistrarEntradaInsumoCommand;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.application.port.LoteInsumoRepositoryPort;
import com.eldiamante360.insumoquimico.application.port.MovimientoInsumoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.exception.InsumoInactivoException;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.insumoquimico.domain.model.LoteInsumo;
import com.eldiamante360.insumoquimico.domain.model.MovimientoInsumo;
import com.eldiamante360.insumoquimico.domain.model.TipoMovimientoInsumo;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegistrarEntradaInsumoService implements RegistrarEntradaInsumoUseCase {

    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;
    private final LoteInsumoRepositoryPort loteInsumoRepositoryPort;
    private final MovimientoInsumoRepositoryPort movimientoInsumoRepositoryPort;

    public RegistrarEntradaInsumoService(InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort,
                                          LoteInsumoRepositoryPort loteInsumoRepositoryPort,
                                          MovimientoInsumoRepositoryPort movimientoInsumoRepositoryPort) {
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
        this.loteInsumoRepositoryPort = loteInsumoRepositoryPort;
        this.movimientoInsumoRepositoryPort = movimientoInsumoRepositoryPort;
    }

    @Override
    public MovimientoInsumoResult ejecutar(RegistrarEntradaInsumoCommand command) {
        InsumoQuimico insumo = insumoQuimicoRepositoryPort.buscarPorId(command.insumoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Insumo quimico", command.insumoId()));

        if (!insumo.isActivo()) {
            throw new InsumoInactivoException(insumo.getNombre());
        }

        LoteInsumo lote = LoteInsumo.nuevo(insumo.getId(), command.numeroLote(), command.fechaVencimiento(),
                command.cantidad());
        LoteInsumo loteGuardado = loteInsumoRepositoryPort.guardar(lote);

        insumo.registrarEntrada(command.cantidad());
        InsumoQuimico insumoActualizado = insumoQuimicoRepositoryPort.guardar(insumo);

        MovimientoInsumo movimiento = MovimientoInsumo.nuevo(insumo.getId(), loteGuardado.getId(),
                TipoMovimientoInsumo.ENTRADA, command.cantidad(), insumoActualizado.getStockActual(),
                command.motivo(), command.usuarioId());

        return MovimientoInsumoAssembler.toResult(movimientoInsumoRepositoryPort.guardar(movimiento));
    }
}
