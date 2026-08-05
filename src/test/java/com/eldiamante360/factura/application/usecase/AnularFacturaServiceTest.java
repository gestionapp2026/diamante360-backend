package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.deudor.application.usecase.AnularCuentaPorCobrarUseCase;
import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.factura.application.port.HistorialFacturaRepositoryPort;
import com.eldiamante360.factura.domain.exception.FacturaYaAnuladaException;
import com.eldiamante360.factura.domain.model.DetalleFactura;
import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.domain.model.Factura;
import com.eldiamante360.factura.domain.model.HistorialFactura;
import com.eldiamante360.factura.domain.model.TipoEventoFactura;
import com.eldiamante360.factura.domain.model.TipoPago;
import com.eldiamante360.inventario.application.port.MovimientoInventarioRepositoryPort;
import com.eldiamante360.inventario.domain.model.MovimientoInventario;
import com.eldiamante360.inventario.domain.model.TipoMovimiento;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.producto.domain.model.TipoVenta;
import com.eldiamante360.producto.domain.model.UnidadMedida;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.domain.model.MedioPago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnularFacturaServiceTest {

    @Mock
    private FacturaRepositoryPort facturaRepositoryPort;

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @Mock
    private HistorialFacturaRepositoryPort historialFacturaRepositoryPort;

    @Mock
    private MovimientoInventarioRepositoryPort movimientoInventarioRepositoryPort;

    @Mock
    private AnularCuentaPorCobrarUseCase anularCuentaPorCobrarUseCase;

    private AnularFacturaService service;

    private CategoriaProducto categoria;

    @BeforeEach
    void setUp() {
        service = new AnularFacturaService(facturaRepositoryPort, productoRepositoryPort,
                historialFacturaRepositoryPort, movimientoInventarioRepositoryPort, anularCuentaPorCobrarUseCase);
        categoria = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
    }

    private Producto producto(Long id, String nombre, BigDecimal stockActual) {
        return new Producto(id, nombre, categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), stockActual, BigDecimal.ONE, true, 0);
    }

    private Factura facturaEmitida(Long id, String numero, List<DetalleFactura> detalles) {
        return facturaEmitida(id, numero, detalles, TipoPago.CONTADO);
    }

    private Factura facturaEmitida(Long id, String numero, List<DetalleFactura> detalles, TipoPago tipoPago) {
        BigDecimal subtotal = detalles.stream().map(DetalleFactura::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal descuento = detalles.stream().map(DetalleFactura::descuento).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = detalles.stream().map(DetalleFactura::total).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Factura(id, numero, 1L, "Juan Perez", "123456789", tipoPago, detalles,
                EstadoFactura.EMITIDA, subtotal, descuento, total, 9L, "Ana Gomez", Instant.now(), null, 0,
                MedioPago.EFECTIVO);
    }

    @Test
    void ejecutar_conFacturaEmitidaYUnDetalle_anulaDevuelveStockYRegistraMovimientoEHistorial() {
        DetalleFactura detalle = DetalleFactura.nuevo(5L, "Chorizo", BigDecimal.valueOf(2), BigDecimal.valueOf(1500), BigDecimal.ZERO);
        Factura factura = facturaEmitida(100L, "FAC-2026-00001", List.of(detalle));
        Producto producto = producto(5L, "Chorizo", BigDecimal.valueOf(8));

        when(facturaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(factura));
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(productoRepositoryPort.guardar(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoInventarioRepositoryPort.guardar(any(MovimientoInventario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(facturaRepositoryPort.guardar(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));
        when(historialFacturaRepositoryPort.guardar(any(HistorialFactura.class))).thenAnswer(inv -> inv.getArgument(0));

        FacturaResult resultado = service.ejecutar(100L, 9L);

        assertThat(resultado.estado()).isEqualTo(EstadoFactura.ANULADA);
        assertThat(resultado.fechaAnulacion()).isNotNull();
        assertThat(producto.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(10));

        ArgumentCaptor<MovimientoInventario> movimientoCaptor = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoInventarioRepositoryPort).guardar(movimientoCaptor.capture());
        MovimientoInventario movimiento = movimientoCaptor.getValue();
        assertThat(movimiento.productoId()).isEqualTo(5L);
        assertThat(movimiento.tipoMovimiento()).isEqualTo(TipoMovimiento.ENTRADA);
        assertThat(movimiento.cantidad()).isEqualByComparingTo(BigDecimal.valueOf(2));
        assertThat(movimiento.stockResultante()).isEqualByComparingTo(BigDecimal.valueOf(10));
        assertThat(movimiento.motivo()).isEqualTo("Devolucion por anulacion de factura FAC-2026-00001");
        assertThat(movimiento.usuarioId()).isEqualTo(9L);

        ArgumentCaptor<Factura> facturaCaptor = ArgumentCaptor.forClass(Factura.class);
        verify(facturaRepositoryPort).guardar(facturaCaptor.capture());
        assertThat(facturaCaptor.getValue().getEstado()).isEqualTo(EstadoFactura.ANULADA);

        ArgumentCaptor<HistorialFactura> historialCaptor = ArgumentCaptor.forClass(HistorialFactura.class);
        verify(historialFacturaRepositoryPort).guardar(historialCaptor.capture());
        HistorialFactura historial = historialCaptor.getValue();
        assertThat(historial.facturaId()).isEqualTo(100L);
        assertThat(historial.tipoEvento()).isEqualTo(TipoEventoFactura.ANULACION);
        assertThat(historial.descripcion()).isEqualTo("Factura FAC-2026-00001 anulada");
        assertThat(historial.usuarioId()).isEqualTo(9L);

        verifyNoInteractions(anularCuentaPorCobrarUseCase);
    }

    @Test
    void ejecutar_conFacturaACreditoEmitida_anulaTambienLaCuentaPorCobrar() {
        DetalleFactura detalle = DetalleFactura.nuevo(5L, "Chorizo", BigDecimal.valueOf(2), BigDecimal.valueOf(1500), BigDecimal.ZERO);
        Factura factura = facturaEmitida(102L, "FAC-2026-00003", List.of(detalle), TipoPago.CREDITO);
        Producto producto = producto(5L, "Chorizo", BigDecimal.valueOf(8));

        when(facturaRepositoryPort.buscarPorId(102L)).thenReturn(Optional.of(factura));
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(productoRepositoryPort.guardar(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoInventarioRepositoryPort.guardar(any(MovimientoInventario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(facturaRepositoryPort.guardar(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));
        when(historialFacturaRepositoryPort.guardar(any(HistorialFactura.class))).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(102L, 9L);

        verify(anularCuentaPorCobrarUseCase).ejecutar(102L, 9L);
    }

    @Test
    void ejecutar_conVariosDetalles_devuelveElStockDeCadaProducto() {
        DetalleFactura detalle1 = DetalleFactura.nuevo(5L, "Chorizo", BigDecimal.valueOf(2), BigDecimal.valueOf(1500), BigDecimal.ZERO);
        DetalleFactura detalle2 = DetalleFactura.nuevo(6L, "Costilla", BigDecimal.valueOf(3), BigDecimal.valueOf(2000), BigDecimal.ZERO);
        Factura factura = facturaEmitida(101L, "FAC-2026-00002", List.of(detalle1, detalle2));
        Producto producto1 = producto(5L, "Chorizo", BigDecimal.valueOf(8));
        Producto producto2 = producto(6L, "Costilla", BigDecimal.valueOf(17));

        when(facturaRepositoryPort.buscarPorId(101L)).thenReturn(Optional.of(factura));
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto1));
        when(productoRepositoryPort.buscarPorId(6L)).thenReturn(Optional.of(producto2));
        when(productoRepositoryPort.guardar(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoInventarioRepositoryPort.guardar(any(MovimientoInventario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(facturaRepositoryPort.guardar(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));
        when(historialFacturaRepositoryPort.guardar(any(HistorialFactura.class))).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(101L, 9L);

        assertThat(producto1.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(10));
        assertThat(producto2.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(20));

        ArgumentCaptor<MovimientoInventario> movimientoCaptor = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoInventarioRepositoryPort, times(2)).guardar(movimientoCaptor.capture());
        List<MovimientoInventario> movimientos = movimientoCaptor.getAllValues();
        assertThat(movimientos).extracting(MovimientoInventario::productoId).containsExactly(5L, 6L);
        assertThat(movimientos).allMatch(m -> m.tipoMovimiento() == TipoMovimiento.ENTRADA);

        verifyNoInteractions(anularCuentaPorCobrarUseCase);
    }

    @Test
    void ejecutar_conFacturaInexistente_lanzaExcepcion() {
        when(facturaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, 9L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verifyNoInteractions(productoRepositoryPort, historialFacturaRepositoryPort, movimientoInventarioRepositoryPort,
                anularCuentaPorCobrarUseCase);
        verify(facturaRepositoryPort, never()).guardar(any());
    }

    @Test
    void ejecutar_conFacturaYaAnulada_lanzaExcepcionYNoRegistraNingunEfectoSecundario() {
        DetalleFactura detalle = DetalleFactura.nuevo(5L, "Chorizo", BigDecimal.valueOf(2), BigDecimal.valueOf(1500), BigDecimal.ZERO);
        Factura facturaAnulada = new Factura(100L, "FAC-2026-00001", 1L, "Juan Perez", "123456789",
                TipoPago.CONTADO, List.of(detalle), EstadoFactura.ANULADA, BigDecimal.valueOf(3000),
                BigDecimal.ZERO, BigDecimal.valueOf(3000), 9L, "Ana Gomez", Instant.now(), Instant.now(), 0,
                MedioPago.EFECTIVO);
        when(facturaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(facturaAnulada));

        assertThatThrownBy(() -> service.ejecutar(100L, 9L))
                .isInstanceOf(FacturaYaAnuladaException.class);

        verifyNoInteractions(productoRepositoryPort, historialFacturaRepositoryPort, movimientoInventarioRepositoryPort,
                anularCuentaPorCobrarUseCase);
        verify(facturaRepositoryPort, never()).guardar(any());
    }
}
