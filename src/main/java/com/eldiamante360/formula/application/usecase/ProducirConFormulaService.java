package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.ConsumoInsumoResult;
import com.eldiamante360.formula.application.dto.ProduccionFormulaResult;
import com.eldiamante360.formula.application.dto.ProducirFormulaCommand;
import com.eldiamante360.formula.application.port.FormulaRepositoryPort;
import com.eldiamante360.formula.domain.exception.CantidadProduccionInvalidaException;
import com.eldiamante360.formula.domain.exception.FormulaInactivaException;
import com.eldiamante360.formula.domain.exception.StockInsumoInsuficienteParaFormulaException;
import com.eldiamante360.formula.domain.model.DetalleFormula;
import com.eldiamante360.formula.domain.model.Formula;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.application.port.LoteInsumoRepositoryPort;
import com.eldiamante360.insumoquimico.application.port.MovimientoInsumoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.insumoquimico.domain.model.LoteInsumo;
import com.eldiamante360.insumoquimico.domain.model.MovimientoInsumo;
import com.eldiamante360.insumoquimico.domain.model.TipoMovimientoInsumo;
import com.eldiamante360.inventario.application.dto.RegistrarMovimientoCommand;
import com.eldiamante360.inventario.application.usecase.RegistrarMovimientoInventarioUseCase;
import com.eldiamante360.inventario.domain.model.TipoMovimiento;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso central del modulo: "produce" una cantidad de un producto
 * usando una formula, lo que descuenta automaticamente del stock de cada
 * quimico involucrado (FEFO, por lote, igual que
 * {@code RegistrarSalidaInsumoService} pero pudiendo repartir el consumo
 * entre varios lotes de un mismo insumo) y registra la cantidad producida
 * como una entrada de inventario del producto.
 */
@Service
@Transactional
public class ProducirConFormulaService implements ProducirConFormulaUseCase {

    private final FormulaRepositoryPort formulaRepositoryPort;
    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;
    private final LoteInsumoRepositoryPort loteInsumoRepositoryPort;
    private final MovimientoInsumoRepositoryPort movimientoInsumoRepositoryPort;
    private final RegistrarMovimientoInventarioUseCase registrarMovimientoInventarioUseCase;

    public ProducirConFormulaService(FormulaRepositoryPort formulaRepositoryPort,
                                      InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort,
                                      LoteInsumoRepositoryPort loteInsumoRepositoryPort,
                                      MovimientoInsumoRepositoryPort movimientoInsumoRepositoryPort,
                                      RegistrarMovimientoInventarioUseCase registrarMovimientoInventarioUseCase) {
        this.formulaRepositoryPort = formulaRepositoryPort;
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
        this.loteInsumoRepositoryPort = loteInsumoRepositoryPort;
        this.movimientoInsumoRepositoryPort = movimientoInsumoRepositoryPort;
        this.registrarMovimientoInventarioUseCase = registrarMovimientoInventarioUseCase;
    }

    private record NecesidadInsumo(DetalleFormula detalle, InsumoQuimico insumo, BigDecimal cantidadNecesaria) {
    }

    @Override
    public ProduccionFormulaResult ejecutar(ProducirFormulaCommand command) {
        if (command.cantidad() == null || command.cantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CantidadProduccionInvalidaException();
        }

        Formula formula = formulaRepositoryPort.buscarPorId(command.formulaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Formula", command.formulaId()));

        if (!formula.isActivo()) {
            throw new FormulaInactivaException(formula.getId());
        }

        BigDecimal factor = command.cantidad().divide(formula.getCantidadBase(), 6, RoundingMode.HALF_UP);

        // Primero se calcula y valida el stock de TODOS los insumos antes de
        // descontar nada, para que la operacion sea todo-o-nada.
        List<NecesidadInsumo> necesidades = new ArrayList<>();
        for (DetalleFormula detalle : formula.getDetalles()) {
            InsumoQuimico insumo = insumoQuimicoRepositoryPort.buscarPorId(detalle.insumoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Insumo quimico", detalle.insumoId()));
            BigDecimal cantidadNecesaria = detalle.cantidad().multiply(factor);
            if (insumo.getStockActual().compareTo(cantidadNecesaria) < 0) {
                throw new StockInsumoInsuficienteParaFormulaException(insumo.getNombre(), insumo.getStockActual(),
                        cantidadNecesaria);
            }
            necesidades.add(new NecesidadInsumo(detalle, insumo, cantidadNecesaria));
        }

        String motivo = "Produccion formula #%d - %s".formatted(formula.getId(), formula.getProductoNombre());

        List<ConsumoInsumoResult> consumos = new ArrayList<>();
        for (NecesidadInsumo necesidad : necesidades) {
            consumirFefo(necesidad.insumo(), necesidad.cantidadNecesaria(), motivo, command.usuarioId());
            consumos.add(new ConsumoInsumoResult(necesidad.detalle().insumoId(), necesidad.detalle().numero(),
                    necesidad.cantidadNecesaria()));
        }

        registrarMovimientoInventarioUseCase.ejecutar(new RegistrarMovimientoCommand(formula.getProductoId(),
                TipoMovimiento.ENTRADA, command.cantidad(), motivo, command.usuarioId()));

        return new ProduccionFormulaResult(formula.getId(), formula.getProductoId(), formula.getProductoNombre(),
                command.cantidad(), consumos);
    }

    /**
     * Consume {@code cantidadRequerida} del insumo descontando de sus lotes
     * en orden FEFO, repartiendo entre varios lotes si es necesario y
     * saltando lotes vencidos. Registra un {@link MovimientoInsumo} de tipo
     * SALIDA por cada porcion de lote consumida.
     */
    private void consumirFefo(InsumoQuimico insumo, BigDecimal cantidadRequerida, String motivo, Long usuarioId) {
        BigDecimal restante = cantidadRequerida;
        LocalDate hoy = LocalDate.now();

        for (LoteInsumo lote : loteInsumoRepositoryPort.listarDisponiblesFefo(insumo.getId())) {
            if (restante.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            if (lote.estaVencido(hoy) || lote.getCantidadActual().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal aConsumir = lote.getCantidadActual().min(restante);
            lote.reducir(aConsumir);
            loteInsumoRepositoryPort.guardar(lote);

            insumo.registrarSalida(aConsumir);
            insumo = insumoQuimicoRepositoryPort.guardar(insumo);

            MovimientoInsumo movimiento = MovimientoInsumo.nuevo(insumo.getId(), lote.getId(),
                    TipoMovimientoInsumo.SALIDA, aConsumir, insumo.getStockActual(), motivo, usuarioId);
            movimientoInsumoRepositoryPort.guardar(movimiento);

            restante = restante.subtract(aConsumir);
        }

        if (restante.compareTo(BigDecimal.ZERO) > 0) {
            // El stock agregado del insumo alcanzaba, pero los lotes vigentes
            // (no vencidos) no cubren la cantidad requerida.
            throw new StockInsumoInsuficienteParaFormulaException(insumo.getNombre(),
                    cantidadRequerida.subtract(restante), cantidadRequerida);
        }
    }
}
