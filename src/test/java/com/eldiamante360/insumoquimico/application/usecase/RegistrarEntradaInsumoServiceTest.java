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
import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarEntradaInsumoServiceTest {

    @Mock
    private InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    @Mock
    private LoteInsumoRepositoryPort loteInsumoRepositoryPort;

    @Mock
    private MovimientoInsumoRepositoryPort movimientoInsumoRepositoryPort;

    private RegistrarEntradaInsumoService service;

    @BeforeEach
    void setUp() {
        service = new RegistrarEntradaInsumoService(insumoQuimicoRepositoryPort, loteInsumoRepositoryPort,
                movimientoInsumoRepositoryPort);
    }

    @Test
    void ejecutar_conInsumoActivo_creaLoteYSumaStock() {
        InsumoQuimico insumo = new InsumoQuimico(5L, "Sal de cura", UnidadMedidaInsumo.KG, BigDecimal.ZERO, true, 0, null);
        RegistrarEntradaInsumoCommand command = new RegistrarEntradaInsumoCommand(5L, "L-001",
                LocalDate.now().plusDays(30), BigDecimal.TEN, "Compra inicial", 1L);

        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumo));
        when(loteInsumoRepositoryPort.guardar(any(LoteInsumo.class))).thenAnswer(invocacion -> {
            LoteInsumo lote = invocacion.getArgument(0);
            return new LoteInsumo(100L, lote.getInsumoId(), lote.getNumeroLote(), lote.getFechaVencimiento(),
                    lote.getCantidadActual(), lote.getFechaIngreso(), 0);
        });
        when(insumoQuimicoRepositoryPort.guardar(any(InsumoQuimico.class))).thenAnswer(invocacion -> {
            InsumoQuimico i = invocacion.getArgument(0);
            return new InsumoQuimico(i.getId(), i.getNombre(), i.getUnidadMedida(), i.getStockActual(), i.isActivo(), 1, i.getPrecioCompra());
        });
        when(movimientoInsumoRepositoryPort.guardar(any(MovimientoInsumo.class))).thenAnswer(invocacion -> {
            MovimientoInsumo m = invocacion.getArgument(0);
            return new MovimientoInsumo(200L, m.insumoId(), m.loteId(), m.tipoMovimiento(), m.cantidad(),
                    m.stockResultante(), m.motivo(), m.usuarioId(), m.fecha());
        });

        MovimientoInsumoResult resultado = service.ejecutar(command);

        assertThat(resultado.id()).isEqualTo(200L);
        assertThat(resultado.loteId()).isEqualTo(100L);
        assertThat(resultado.tipoMovimiento()).isEqualTo(TipoMovimientoInsumo.ENTRADA);
        assertThat(resultado.stockResultante()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void ejecutar_conInsumoInactivo_lanzaExcepcion() {
        InsumoQuimico insumo = new InsumoQuimico(5L, "Sal de cura", UnidadMedidaInsumo.KG, BigDecimal.ZERO, false, 0, null);
        RegistrarEntradaInsumoCommand command = new RegistrarEntradaInsumoCommand(5L, "L-001", null,
                BigDecimal.TEN, "Compra inicial", 1L);
        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumo));

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(InsumoInactivoException.class);
    }

    @Test
    void ejecutar_conInsumoInexistente_lanzaExcepcion() {
        RegistrarEntradaInsumoCommand command = new RegistrarEntradaInsumoCommand(404L, "L-001", null,
                BigDecimal.TEN, "Compra inicial", 1L);
        when(insumoQuimicoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
