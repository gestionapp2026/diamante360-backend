package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.ActualizarFormulaCommand;
import com.eldiamante360.formula.application.dto.DetalleFormulaCommand;
import com.eldiamante360.formula.application.dto.FormulaResult;
import com.eldiamante360.formula.application.port.FormulaRepositoryPort;
import com.eldiamante360.formula.domain.model.DetalleFormula;
import com.eldiamante360.formula.domain.model.Formula;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ActualizarFormulaService implements ActualizarFormulaUseCase {

    private final FormulaRepositoryPort formulaRepositoryPort;
    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    public ActualizarFormulaService(FormulaRepositoryPort formulaRepositoryPort,
                                     InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort) {
        this.formulaRepositoryPort = formulaRepositoryPort;
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
    }

    @Override
    public FormulaResult ejecutar(ActualizarFormulaCommand command) {
        Formula formula = formulaRepositoryPort.buscarPorId(command.formulaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Formula", command.formulaId()));

        List<DetalleFormula> detalles = new ArrayList<>();
        for (DetalleFormulaCommand detalleCommand : command.detalles()) {
            InsumoQuimico insumo = insumoQuimicoRepositoryPort.buscarPorId(detalleCommand.insumoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Insumo quimico", detalleCommand.insumoId()));
            detalles.add(DetalleFormula.nuevo(insumo.getId(), detalleCommand.numero(), detalleCommand.cantidad()));
        }

        formula.actualizar(command.cantidadBase(), command.unidadBase(), detalles);

        return FormulaAssembler.toResult(formulaRepositoryPort.guardar(formula), insumoQuimicoRepositoryPort);
    }
}
