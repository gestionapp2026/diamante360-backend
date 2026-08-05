package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.CrearFormulaCommand;
import com.eldiamante360.formula.application.dto.DetalleFormulaCommand;
import com.eldiamante360.formula.application.dto.FormulaResult;
import com.eldiamante360.formula.application.port.FormulaRepositoryPort;
import com.eldiamante360.formula.domain.exception.FormulaDuplicadaParaProductoException;
import com.eldiamante360.formula.domain.model.DetalleFormula;
import com.eldiamante360.formula.domain.model.Formula;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CrearFormulaService implements CrearFormulaUseCase {

    private final FormulaRepositoryPort formulaRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    public CrearFormulaService(FormulaRepositoryPort formulaRepositoryPort,
                                ProductoRepositoryPort productoRepositoryPort,
                                InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort) {
        this.formulaRepositoryPort = formulaRepositoryPort;
        this.productoRepositoryPort = productoRepositoryPort;
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
    }

    @Override
    public FormulaResult ejecutar(CrearFormulaCommand command) {
        Producto producto = productoRepositoryPort.buscarPorId(command.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", command.productoId()));

        if (formulaRepositoryPort.existePorProductoId(producto.getId())) {
            throw new FormulaDuplicadaParaProductoException(producto.getId());
        }

        List<DetalleFormula> detalles = new ArrayList<>();
        for (DetalleFormulaCommand detalleCommand : command.detalles()) {
            InsumoQuimico insumo = insumoQuimicoRepositoryPort.buscarPorId(detalleCommand.insumoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Insumo quimico", detalleCommand.insumoId()));
            detalles.add(DetalleFormula.nuevo(insumo.getId(), detalleCommand.numero(), detalleCommand.cantidad()));
        }

        Formula formula = Formula.nueva(producto.getId(), producto.getNombre(), command.cantidadBase(),
                command.unidadBase(), detalles);
        Formula guardada = formulaRepositoryPort.guardar(formula);

        return FormulaAssembler.toResult(guardada, insumoQuimicoRepositoryPort);
    }
}
