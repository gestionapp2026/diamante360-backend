package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.PrecioClienteProductoRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
import com.eldiamante360.deudor.application.dto.RegistrarCreditoCommand;
import com.eldiamante360.deudor.application.usecase.RegistrarCreditoUseCase;
import com.eldiamante360.factura.application.dto.CrearFacturaCommand;
import com.eldiamante360.factura.application.dto.DetalleFacturaCommand;
import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.factura.application.port.HistorialFacturaRepositoryPort;
import com.eldiamante360.factura.domain.exception.ClienteInactivoException;
import com.eldiamante360.factura.domain.exception.DetalleFacturaVacioException;
import com.eldiamante360.factura.domain.exception.ProductoInactivoException;
import com.eldiamante360.factura.domain.model.Factura;
import com.eldiamante360.factura.domain.model.HistorialFactura;
import com.eldiamante360.factura.domain.model.TipoEventoFactura;
import com.eldiamante360.factura.domain.model.TipoPago;
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
import com.eldiamante360.shared.domain.model.MedioPago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearFacturaServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @Mock
    private FacturaRepositoryPort facturaRepositoryPort;

    @Mock
    private HistorialFacturaRepositoryPort historialFacturaRepositoryPort;

    @Mock
    private MovimientoInventarioRepositoryPort movimientoInventarioRepositoryPort;

    @Mock
    private RegistrarCreditoUseCase registrarCreditoUseCase;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PrecioClienteProductoRepositoryPort precioClienteProductoRepositoryPort;

    private CrearFacturaService service;

    private Cliente cliente;
    private CategoriaProducto categoria;

    @BeforeEach
    void setUp() {
        service = new CrearFacturaService(clienteRepositoryPort, productoRepositoryPort, facturaRepositoryPort,
                historialFacturaRepositoryPort, movimientoInventarioRepositoryPort, registrarCreditoUseCase,
                usuarioRepositoryPort, precioClienteProductoRepositoryPort);
        cliente = new Cliente(1L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", "3001234567",
                "juan@correo.com", "Calle 1 # 2-3", null, true, 0);
        categoria = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
        Usuario usuario = new Usuario(9L, "vendedor1", "hash", "Ana Gomez", null, true, false, null, 0);
        lenient().when(usuarioRepositoryPort.buscarPorId(9L)).thenReturn(Optional.of(usuario));
        lenient().when(precioClienteProductoRepositoryPort.buscarPorClienteYProducto(any(), any()))
                .thenReturn(Optional.empty());
    }

    private Producto productoActivo(Long id, String nombre, BigDecimal precioVenta, BigDecimal stockActual) {
        return new Producto(id, nombre, categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), precioVenta, stockActual, BigDecimal.ONE, true, 0);
    }

    @Test
    void ejecutar_conUnDetalle_generaFacturaDescuentaStockYRegistraMovimientoYHistorial() {
        Producto producto = productoActivo(5L, "Chorizo", BigDecimal.valueOf(1500), BigDecimal.TEN);
        DetalleFacturaCommand detalleCommand = new DetalleFacturaCommand(5L, BigDecimal.valueOf(2), BigDecimal.ZERO);
        CrearFacturaCommand command = new CrearFacturaCommand(1L, TipoPago.CONTADO, List.of(detalleCommand), 9L,
                MedioPago.EFECTIVO);

        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(facturaRepositoryPort.siguienteNumero()).thenReturn("FAC-2026-00001");
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(productoRepositoryPort.guardar(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoInventarioRepositoryPort.guardar(any(MovimientoInventario.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(facturaRepositoryPort.guardar(any(Factura.class))).thenAnswer(inv -> {
            Factura f = inv.getArgument(0);
            return new Factura(100L, f.getNumero(), f.getClienteId(), f.getClienteNombre(),
                    f.getClienteNumeroDocumento(), f.getTipoPago(), f.getDetalles(), f.getEstado(), f.getSubtotal(),
                    f.getDescuento(), f.getTotal(), f.getUsuarioId(), f.getUsuarioNombre(), f.getFecha(),
                    f.getFechaAnulacion(), 0, f.getMedioPago());
        });
        when(historialFacturaRepositoryPort.guardar(any(HistorialFactura.class))).thenAnswer(inv -> inv.getArgument(0));

        FacturaResult resultado = service.ejecutar(command);

        assertThat(resultado.id()).isEqualTo(100L);
        assertThat(resultado.numero()).isEqualTo("FAC-2026-00001");
        assertThat(resultado.clienteId()).isEqualTo(1L);
        assertThat(resultado.clienteNombre()).isEqualTo("Juan Perez");
        assertThat(resultado.clienteNumeroDocumento()).isEqualTo("123456789");
        assertThat(resultado.detalles()).hasSize(1);
        assertThat(resultado.total()).isEqualByComparingTo(BigDecimal.valueOf(3000).setScale(2));

        assertThat(producto.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(8));

        ArgumentCaptor<MovimientoInventario> movimientoCaptor = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoInventarioRepositoryPort).guardar(movimientoCaptor.capture());
        MovimientoInventario movimiento = movimientoCaptor.getValue();
        assertThat(movimiento.productoId()).isEqualTo(5L);
        assertThat(movimiento.tipoMovimiento()).isEqualTo(TipoMovimiento.SALIDA);
        assertThat(movimiento.cantidad()).isEqualByComparingTo(BigDecimal.valueOf(2));
        assertThat(movimiento.stockResultante()).isEqualByComparingTo(BigDecimal.valueOf(8));
        assertThat(movimiento.motivo()).isEqualTo("Salida por factura FAC-2026-00001");
        assertThat(movimiento.usuarioId()).isEqualTo(9L);

        ArgumentCaptor<Factura> facturaCaptor = ArgumentCaptor.forClass(Factura.class);
        verify(facturaRepositoryPort).guardar(facturaCaptor.capture());
        Factura facturaGuardada = facturaCaptor.getValue();
        assertThat(facturaGuardada.getNumero()).isEqualTo("FAC-2026-00001");
        assertThat(facturaGuardada.getClienteId()).isEqualTo(1L);
        assertThat(facturaGuardada.getTipoPago()).isEqualTo(TipoPago.CONTADO);
        assertThat(facturaGuardada.getUsuarioId()).isEqualTo(9L);
        assertThat(facturaGuardada.getUsuarioNombre()).isEqualTo("Ana Gomez");
        assertThat(facturaGuardada.getDetalles()).hasSize(1);
        assertThat(facturaGuardada.getDetalles().get(0).productoId()).isEqualTo(5L);
        assertThat(facturaGuardada.getDetalles().get(0).productoNombre()).isEqualTo("Chorizo");
        assertThat(facturaGuardada.getDetalles().get(0).precioUnitario()).isEqualByComparingTo(BigDecimal.valueOf(1500));

        ArgumentCaptor<HistorialFactura> historialCaptor = ArgumentCaptor.forClass(HistorialFactura.class);
        verify(historialFacturaRepositoryPort).guardar(historialCaptor.capture());
        HistorialFactura historial = historialCaptor.getValue();
        assertThat(historial.facturaId()).isEqualTo(100L);
        assertThat(historial.tipoEvento()).isEqualTo(TipoEventoFactura.CREACION);
        assertThat(historial.descripcion()).isEqualTo("Factura FAC-2026-00001 generada");
        assertThat(historial.usuarioId()).isEqualTo(9L);

        verifyNoInteractions(registrarCreditoUseCase);
    }

    @Test
    void ejecutar_conVariosDetalles_descuentaCadaProductoYAgregaLosTotales() {
        Producto producto1 = productoActivo(5L, "Chorizo", BigDecimal.valueOf(1500), BigDecimal.TEN);
        Producto producto2 = productoActivo(6L, "Costilla", BigDecimal.valueOf(2000), BigDecimal.valueOf(20));
        DetalleFacturaCommand detalle1 = new DetalleFacturaCommand(5L, BigDecimal.valueOf(2), BigDecimal.ZERO);
        DetalleFacturaCommand detalle2 = new DetalleFacturaCommand(6L, BigDecimal.valueOf(3), BigDecimal.TEN);
        CrearFacturaCommand command = new CrearFacturaCommand(1L, TipoPago.CREDITO, List.of(detalle1, detalle2), 9L,
                null);

        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(facturaRepositoryPort.siguienteNumero()).thenReturn("FAC-2026-00002");
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto1));
        when(productoRepositoryPort.buscarPorId(6L)).thenReturn(Optional.of(producto2));
        when(productoRepositoryPort.guardar(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoInventarioRepositoryPort.guardar(any(MovimientoInventario.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(facturaRepositoryPort.guardar(any(Factura.class))).thenAnswer(inv -> {
            Factura f = inv.getArgument(0);
            return new Factura(101L, f.getNumero(), f.getClienteId(), f.getClienteNombre(),
                    f.getClienteNumeroDocumento(), f.getTipoPago(), f.getDetalles(), f.getEstado(), f.getSubtotal(),
                    f.getDescuento(), f.getTotal(), f.getUsuarioId(), f.getUsuarioNombre(), f.getFecha(),
                    f.getFechaAnulacion(), 0, f.getMedioPago());
        });
        when(historialFacturaRepositoryPort.guardar(any(HistorialFactura.class))).thenAnswer(inv -> inv.getArgument(0));

        FacturaResult resultado = service.ejecutar(command);

        assertThat(producto1.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(8));
        assertThat(producto2.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(17));

        ArgumentCaptor<MovimientoInventario> movimientoCaptor = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoInventarioRepositoryPort, org.mockito.Mockito.times(2)).guardar(movimientoCaptor.capture());
        List<MovimientoInventario> movimientos = movimientoCaptor.getAllValues();
        assertThat(movimientos).hasSize(2);
        assertThat(movimientos.get(0).productoId()).isEqualTo(5L);
        assertThat(movimientos.get(1).productoId()).isEqualTo(6L);
        assertThat(movimientos).allMatch(m -> m.tipoMovimiento() == TipoMovimiento.SALIDA);

        BigDecimal subtotalEsperado = BigDecimal.valueOf(3000).setScale(2).add(BigDecimal.valueOf(6000).setScale(2));
        BigDecimal descuentoEsperado = BigDecimal.ZERO.setScale(2).add(BigDecimal.valueOf(600).setScale(2));
        assertThat(resultado.subtotal()).isEqualByComparingTo(subtotalEsperado);
        assertThat(resultado.descuento()).isEqualByComparingTo(descuentoEsperado);
        assertThat(resultado.total()).isEqualByComparingTo(subtotalEsperado.subtract(descuentoEsperado));
        assertThat(resultado.detalles()).hasSize(2);

        ArgumentCaptor<RegistrarCreditoCommand> creditoCaptor = ArgumentCaptor.forClass(RegistrarCreditoCommand.class);
        verify(registrarCreditoUseCase).ejecutar(creditoCaptor.capture());
        RegistrarCreditoCommand creditoCommand = creditoCaptor.getValue();
        assertThat(creditoCommand.facturaId()).isEqualTo(101L);
        assertThat(creditoCommand.numeroFactura()).isEqualTo("FAC-2026-00002");
        assertThat(creditoCommand.clienteId()).isEqualTo(1L);
        assertThat(creditoCommand.clienteNombre()).isEqualTo("Juan Perez");
        assertThat(creditoCommand.clienteNumeroDocumento()).isEqualTo("123456789");
        assertThat(creditoCommand.monto()).isEqualByComparingTo(resultado.total());
        assertThat(creditoCommand.usuarioId()).isEqualTo(9L);
    }

    @Test
    void ejecutar_conListaDeDetallesVacia_lanzaExcepcionYNoInteractuaConLosPuertos() {
        CrearFacturaCommand command = new CrearFacturaCommand(1L, TipoPago.CONTADO, List.of(), 9L, MedioPago.EFECTIVO);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(DetalleFacturaVacioException.class);

        verifyNoInteractions(clienteRepositoryPort, productoRepositoryPort, facturaRepositoryPort,
                historialFacturaRepositoryPort, movimientoInventarioRepositoryPort, registrarCreditoUseCase);
    }

    @Test
    void ejecutar_conListaDeDetallesNula_lanzaExcepcion() {
        CrearFacturaCommand command = new CrearFacturaCommand(1L, TipoPago.CONTADO, null, 9L, MedioPago.EFECTIVO);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(DetalleFacturaVacioException.class);
    }

    @Test
    void ejecutar_conClienteInexistente_lanzaExcepcion() {
        DetalleFacturaCommand detalleCommand = new DetalleFacturaCommand(5L, BigDecimal.valueOf(2), BigDecimal.ZERO);
        CrearFacturaCommand command = new CrearFacturaCommand(404L, TipoPago.CONTADO, List.of(detalleCommand), 9L, MedioPago.EFECTIVO);
        when(clienteRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verifyNoInteractions(facturaRepositoryPort, historialFacturaRepositoryPort, movimientoInventarioRepositoryPort,
                registrarCreditoUseCase);
    }

    @Test
    void ejecutar_conClienteInactivo_lanzaExcepcion() {
        Cliente clienteInactivo = new Cliente(1L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", "3001234567",
                "juan@correo.com", "Calle 1 # 2-3", null, false, 0);
        DetalleFacturaCommand detalleCommand = new DetalleFacturaCommand(5L, BigDecimal.valueOf(2), BigDecimal.ZERO);
        CrearFacturaCommand command = new CrearFacturaCommand(1L, TipoPago.CONTADO, List.of(detalleCommand), 9L, MedioPago.EFECTIVO);
        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(clienteInactivo));

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(ClienteInactivoException.class);

        verifyNoInteractions(facturaRepositoryPort, historialFacturaRepositoryPort, movimientoInventarioRepositoryPort,
                registrarCreditoUseCase);
    }

    @Test
    void ejecutar_conProductoInexistente_lanzaExcepcion() {
        DetalleFacturaCommand detalleCommand = new DetalleFacturaCommand(404L, BigDecimal.valueOf(2), BigDecimal.ZERO);
        CrearFacturaCommand command = new CrearFacturaCommand(1L, TipoPago.CONTADO, List.of(detalleCommand), 9L, MedioPago.EFECTIVO);
        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(facturaRepositoryPort.siguienteNumero()).thenReturn("FAC-2026-00001");
        when(productoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(facturaRepositoryPort, never()).guardar(any());
        verifyNoInteractions(historialFacturaRepositoryPort, movimientoInventarioRepositoryPort, registrarCreditoUseCase);
    }

    @Test
    void ejecutar_conProductoInactivo_lanzaExcepcion() {
        Producto productoInactivo = new Producto(5L, "Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE, false, 0);
        DetalleFacturaCommand detalleCommand = new DetalleFacturaCommand(5L, BigDecimal.valueOf(2), BigDecimal.ZERO);
        CrearFacturaCommand command = new CrearFacturaCommand(1L, TipoPago.CONTADO, List.of(detalleCommand), 9L, MedioPago.EFECTIVO);
        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(facturaRepositoryPort.siguienteNumero()).thenReturn("FAC-2026-00001");
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(productoInactivo));

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(ProductoInactivoException.class);

        verify(facturaRepositoryPort, never()).guardar(any());
        verifyNoInteractions(historialFacturaRepositoryPort, movimientoInventarioRepositoryPort, registrarCreditoUseCase);
    }

    @Test
    void ejecutar_conStockInsuficiente_propagaLaExcepcionYNoGuardaNadaMas() {
        Producto producto = productoActivo(5L, "Chorizo", BigDecimal.valueOf(1500), BigDecimal.valueOf(1));
        DetalleFacturaCommand detalleCommand = new DetalleFacturaCommand(5L, BigDecimal.valueOf(20), BigDecimal.ZERO);
        CrearFacturaCommand command = new CrearFacturaCommand(1L, TipoPago.CONTADO, List.of(detalleCommand), 9L, MedioPago.EFECTIVO);
        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(facturaRepositoryPort.siguienteNumero()).thenReturn("FAC-2026-00001");
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(StockInsuficienteException.class);

        assertThat(producto.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(1));
        verify(productoRepositoryPort, never()).guardar(any());
        verify(facturaRepositoryPort, never()).guardar(any());
        verifyNoInteractions(historialFacturaRepositoryPort, movimientoInventarioRepositoryPort);
    }
}
