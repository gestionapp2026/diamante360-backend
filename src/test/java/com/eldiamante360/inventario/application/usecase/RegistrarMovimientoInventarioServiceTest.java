package com.eldiamante360.inventario.application.usecase;

import com.eldiamante360.inventario.application.dto.MovimientoResult;
import com.eldiamante360.inventario.application.dto.RegistrarMovimientoCommand;
import com.eldiamante360.inventario.application.port.MovimientoInventarioRepositoryPort;
import com.eldiamante360.inventario.domain.model.MovimientoInventario;
import com.eldiamante360.inventario.domain.model.TipoMovimiento;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.exception.StockInsuficienteException;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.producto.domain.model.TipoVenta;
import com.eldiamante360.producto.domain.model.UnidadMedida;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarMovimientoInventarioServiceTest {

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @Mock
    private MovimientoInventarioRepositoryPort movimientoInventarioRepositoryPort;

    private RegistrarMovimientoInventarioService service;

    private Producto producto;

    @BeforeEach
    void setUp() {
        service = new RegistrarMovimientoInventarioService(productoRepositoryPort, movimientoInventarioRepositoryPort);
        CategoriaProducto categoria = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
        producto = new Producto(5L, "Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE, true, 0);
    }

    @Test
    void ejecutar_conEntrada_sumaAlStockYRegistraElMovimiento() {
        RegistrarMovimientoCommand command = new RegistrarMovimientoCommand(5L, TipoMovimiento.ENTRADA,
                BigDecimal.valueOf(5), "Compra a proveedor", 1L);
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(productoRepositoryPort.guardar(any(Producto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(movimientoInventarioRepositoryPort.guardar(any(MovimientoInventario.class))).thenAnswer(invocacion -> {
            MovimientoInventario m = invocacion.getArgument(0);
            return new MovimientoInventario(100L, m.productoId(), m.tipoMovimiento(), m.cantidad(),
                    m.stockResultante(), m.motivo(), m.usuarioId(), m.fecha());
        });

        MovimientoResult resultado = service.ejecutar(command);

        assertThat(resultado.id()).isEqualTo(100L);
        assertThat(resultado.stockResultante()).isEqualByComparingTo(BigDecimal.valueOf(15));
        assertThat(resultado.tipoMovimiento()).isEqualTo(TipoMovimiento.ENTRADA);
    }

    @Test
    void ejecutar_conSalidaYStockSuficiente_restaDelStock() {
        RegistrarMovimientoCommand command = new RegistrarMovimientoCommand(5L, TipoMovimiento.SALIDA,
                BigDecimal.valueOf(4), "Venta", 1L);
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(productoRepositoryPort.guardar(any(Producto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(movimientoInventarioRepositoryPort.guardar(any(MovimientoInventario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        MovimientoResult resultado = service.ejecutar(command);

        assertThat(resultado.stockResultante()).isEqualByComparingTo(BigDecimal.valueOf(6));
    }

    @Test
    void ejecutar_conSalidaYStockInsuficiente_lanzaExcepcionYNoGuardaMovimiento() {
        RegistrarMovimientoCommand command = new RegistrarMovimientoCommand(5L, TipoMovimiento.SALIDA,
                BigDecimal.valueOf(50), "Venta", 1L);
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(StockInsuficienteException.class);

        verify(movimientoInventarioRepositoryPort, org.mockito.Mockito.never()).guardar(any());
    }

    @Test
    void ejecutar_conAjuste_estableceElStockAbsoluto() {
        RegistrarMovimientoCommand command = new RegistrarMovimientoCommand(5L, TipoMovimiento.AJUSTE,
                BigDecimal.valueOf(3), "Conteo fisico", 1L);
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(productoRepositoryPort.guardar(any(Producto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(movimientoInventarioRepositoryPort.guardar(any(MovimientoInventario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        MovimientoResult resultado = service.ejecutar(command);

        assertThat(resultado.stockResultante()).isEqualByComparingTo(BigDecimal.valueOf(3));
    }

    @Test
    void ejecutar_conProductoInexistente_lanzaExcepcion() {
        RegistrarMovimientoCommand command = new RegistrarMovimientoCommand(404L, TipoMovimiento.ENTRADA,
                BigDecimal.valueOf(5), "Compra", 1L);
        when(productoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
