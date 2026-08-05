package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.MovimientoInsumoResult;
import com.eldiamante360.insumoquimico.application.dto.RegistrarSalidaInsumoCommand;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.application.port.LoteInsumoRepositoryPort;
import com.eldiamante360.insumoquimico.application.port.MovimientoInsumoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.exception.InsumoInactivoException;
import com.eldiamante360.insumoquimico.domain.exception.LoteVencidoException;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.insumoquimico.domain.model.LoteInsumo;
import com.eldiamante360.insumoquimico.domain.model.MovimientoInsumo;
import com.eldiamante360.insumoquimico.domain.model.TipoMovimientoInsumo;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class RegistrarSalidaInsumoService implements RegistrarSalidaInsumoUseCase {

    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;
    private final LoteInsumoRepositoryPort loteInsumoRepositoryPort;
    private final MovimientoInsumoRepositoryPort movimientoInsumoRepositoryPort;

    public RegistrarSalidaInsumoService(InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort,
                                         LoteInsumoRepositoryPort loteInsumoRepositoryPort,
                                         MovimientoInsumoRepositoryPort movimientoInsumoRepositoryPort) {
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
        this.loteInsumoRepositoryPort = loteInsumoRepositoryPort;
        this.movimientoInsumoRepositoryPort = movimientoInsumoRepositoryPort;
    }

    @Override
    public MovimientoInsumoResult ejecutar(RegistrarSalidaInsumoCommand command) {
        InsumoQuimico insumo = insumoQuimicoRepositoryPort.buscarPorId(command.insumoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Insumo quimico", command.insumoId()));

        if (!insumo.isActivo()) {
            throw new InsumoInactivoException(insumo.getNombre());
        }

        LoteInsumo lote = loteInsumoRepositoryPort.buscarPorId(command.loteId())
                .filter(l -> l.getInsumoId().equals(insumo.getId()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote de insumo", command.loteId()));

        if (lote.estaVencido(LocalDate.now())) {
            throw new LoteVencidoException(lote.getId(), lote.getFechaVencimiento());
        }

        lote.reducir(command.cantidad());
        loteInsumoRepositoryPort.guardar(lote);

        insumo.registrarSalida(command.cantidad());
        InsumoQuimico insumoActualizado = insumoQuimicoRepositoryPort.guardar(insumo);

        MovimientoInsumo movimiento = MovimientoInsumo.nuevo(insumo.getId(), lote.getId(),
                TipoMovimientoInsumo.SALIDA, command.cantidad(), insumoActualizado.getStockActual(),
                command.motivo(), command.usuarioId());

        return MovimientoInsumoAssembler.toResult(movimientoInsumoRepositoryPort.guardar(movimiento));
    }
}
