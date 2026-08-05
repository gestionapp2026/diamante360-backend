package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.MovimientoInsumoResult;
import com.eldiamante360.insumoquimico.application.dto.RegistrarSalidaInsumoCommand;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.application.port.LoteInsumoRepositoryPort;
import com.eldiamante360.insumoquimico.application.port.MovimientoInsumoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.exception.InsumoInactivoException;
import com.eldiamante360.insumoquimico.domain.exception.LoteVencidoException;
import com.eldiamante360.insumoquimico.domain.exception.StockLoteInsuficienteException;
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
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarSalidaInsumoServiceTest {

    @Mock
    private InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    @Mock
    private LoteInsumoRepositoryPort loteInsumoRepositoryPort;

    @Mock
    private MovimientoInsumoRepositoryPort movimientoInsumoRepositoryPort;

    private RegistrarSalidaInsumoService service;

    private InsumoQuimico insumoActivo;

    @BeforeEach
    void setUp() {
        service = new RegistrarSalidaInsumoService(insumoQuimicoRepositoryPort, loteInsumoRepositoryPort,
                movimientoInsumoRepositoryPort);
        insumoActivo = new InsumoQuimico(5L, "Sal de cura", UnidadMedidaInsumo.KG, BigDecimal.TEN, true, 0, null);
    }

    @Test
    void ejecutar_conLoteValidoYStockSuficiente_restaDelLoteYDelStock() {
        LoteInsumo lote = new LoteInsumo(100L, 5L, "L-001", LocalDate.now().plusDays(30), BigDecimal.TEN,
                Instant.now(), 0);
        RegistrarSalidaInsumoCommand command = new RegistrarSalidaInsumoCommand(5L, 100L, BigDecimal.valueOf(4),
                "Uso en produccion", 1L);

        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumoActivo));
        when(loteInsumoRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(lote));
        when(loteInsumoRepositoryPort.guardar(any(LoteInsumo.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(insumoQuimicoRepositoryPort.guardar(any(InsumoQuimico.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(movimientoInsumoRepositoryPort.guardar(any(MovimientoInsumo.class))).thenAnswer(invocacion -> {
            MovimientoInsumo m = invocacion.getArgument(0);
            return new MovimientoInsumo(200L, m.insumoId(), m.loteId(), m.tipoMovimiento(), m.cantidad(),
                    m.stockResultante(), m.motivo(), m.usuarioId(), m.fecha());
        });

        MovimientoInsumoResult resultado = service.ejecutar(command);

        assertThat(resultado.tipoMovimiento()).isEqualTo(TipoMovimientoInsumo.SALIDA);
        assertThat(resultado.stockResultante()).isEqualByComparingTo(BigDecimal.valueOf(6));
        assertThat(lote.getCantidadActual()).isEqualByComparingTo(BigDecimal.valueOf(6));
    }

    @Test
    void ejecutar_conLoteVencido_lanzaExcepcionYNoRegistraMovimiento() {
        LoteInsumo loteVencido = new LoteInsumo(100L, 5L, "L-001", LocalDate.now().minusDays(1), BigDecimal.TEN,
                Instant.now(), 0);
        RegistrarSalidaInsumoCommand command = new RegistrarSalidaInsumoCommand(5L, 100L, BigDecimal.valueOf(4),
                "Uso en produccion", 1L);

        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumoActivo));
        when(loteInsumoRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(loteVencido));

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(LoteVencidoException.class);

        verify(movimientoInsumoRepositoryPort, never()).guardar(any(MovimientoInsumo.class));
    }

    @Test
    void ejecutar_conStockDeLoteInsuficiente_lanzaExcepcion() {
        LoteInsumo lote = new LoteInsumo(100L, 5L, "L-001", LocalDate.now().plusDays(30), BigDecimal.valueOf(2),
                Instant.now(), 0);
        RegistrarSalidaInsumoCommand command = new RegistrarSalidaInsumoCommand(5L, 100L, BigDecimal.TEN,
                "Uso en produccion", 1L);

        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumoActivo));
        when(loteInsumoRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(lote));

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(StockLoteInsuficienteException.class);
    }

    @Test
    void ejecutar_conInsumoInactivo_lanzaExcepcion() {
        InsumoQuimico insumoInactivo = new InsumoQuimico(5L, "Sal de cura", UnidadMedidaInsumo.KG, BigDecimal.TEN,
                false, 0, null);
        RegistrarSalidaInsumoCommand command = new RegistrarSalidaInsumoCommand(5L, 100L, BigDecimal.valueOf(4),
                "Uso en produccion", 1L);
        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumoInactivo));

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(InsumoInactivoException.class);
    }

    @Test
    void ejecutar_conLoteDeOtroInsumo_lanzaExcepcion() {
        LoteInsumo loteDeOtroInsumo = new LoteInsumo(100L, 99L, "L-001", LocalDate.now().plusDays(30), BigDecimal.TEN,
                Instant.now(), 0);
        RegistrarSalidaInsumoCommand command = new RegistrarSalidaInsumoCommand(5L, 100L, BigDecimal.valueOf(4),
                "Uso en produccion", 1L);

        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumoActivo));
        when(loteInsumoRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(loteDeOtroInsumo));

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void ejecutar_conInsumoInexistente_lanzaExcepcion() {
        RegistrarSalidaInsumoCommand command = new RegistrarSalidaInsumoCommand(404L, 100L, BigDecimal.valueOf(4),
                "Uso en produccion", 1L);
        when(insumoQuimicoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
