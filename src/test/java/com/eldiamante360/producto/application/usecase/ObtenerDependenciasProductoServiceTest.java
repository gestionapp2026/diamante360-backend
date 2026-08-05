package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.factura.infrastructure.persistence.repository.DetalleFacturaJpaRepository;
import com.eldiamante360.formula.infrastructure.persistence.repository.FormulaJpaRepository;
import com.eldiamante360.inventario.infrastructure.persistence.repository.MovimientoInventarioJpaRepository;
import com.eldiamante360.orden.infrastructure.persistence.repository.DetalleOrdenJpaRepository;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerDependenciasProductoServiceTest {

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @Mock
    private DetalleFacturaJpaRepository detalleFacturaJpaRepository;

    @Mock
    private DetalleOrdenJpaRepository detalleOrdenJpaRepository;

    @Mock
    private MovimientoInventarioJpaRepository movimientoInventarioJpaRepository;

    @Mock
    private FormulaJpaRepository formulaJpaRepository;

    private ObtenerDependenciasProductoService service;

    private Producto producto;

    @BeforeEach
    void setUp() {
        service = new ObtenerDependenciasProductoService(productoRepositoryPort, detalleFacturaJpaRepository,
                detalleOrdenJpaRepository, movimientoInventarioJpaRepository, formulaJpaRepository);
        CategoriaProducto categoria = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
        producto = new Producto(5L, "Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE, true, 0);
    }

    private void sinDependencias() {
        lenient().when(detalleFacturaJpaRepository.countByProductoId(5L)).thenReturn(0L);
        lenient().when(detalleOrdenJpaRepository.countByProductoId(5L)).thenReturn(0L);
        lenient().when(movimientoInventarioJpaRepository.countByProductoId(5L)).thenReturn(0L);
        lenient().when(formulaJpaRepository.existsByProductoId(5L)).thenReturn(false);
    }

    @Test
    void ejecutar_conFacturasAsociadas_devuelveBloqueadoConMensaje() {
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        sinDependencias();
        when(detalleFacturaJpaRepository.countByProductoId(5L)).thenReturn(2L);

        var resultado = service.ejecutar(5L);

        assertThat(resultado.tieneDependencias()).isTrue();
        assertThat(resultado.bloqueado()).isTrue();
        assertThat(resultado.mensajeBloqueo()).isEqualTo(EliminarProductoService.MENSAJE_BLOQUEO_NO_CASCADEABLE);
        assertThat(resultado.dependencias()).hasSize(1);
    }

    @Test
    void ejecutar_conOrdenesAsociadas_devuelveBloqueadoConMensaje() {
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        sinDependencias();
        when(detalleOrdenJpaRepository.countByProductoId(5L)).thenReturn(1L);

        var resultado = service.ejecutar(5L);

        assertThat(resultado.bloqueado()).isTrue();
        assertThat(resultado.mensajeBloqueo()).isEqualTo(EliminarProductoService.MENSAJE_BLOQUEO_NO_CASCADEABLE);
    }

    @Test
    void ejecutar_conSoloMovimientosYFormula_devuelveConteosSinBloqueo() {
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        sinDependencias();
        when(movimientoInventarioJpaRepository.countByProductoId(5L)).thenReturn(4L);
        when(formulaJpaRepository.existsByProductoId(5L)).thenReturn(true);

        var resultado = service.ejecutar(5L);

        assertThat(resultado.tieneDependencias()).isTrue();
        assertThat(resultado.bloqueado()).isFalse();
        assertThat(resultado.mensajeBloqueo()).isNull();
        assertThat(resultado.dependencias()).hasSize(2);
    }

    @Test
    void ejecutar_sinDependencias_devuelveVacioSinBloqueo() {
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        sinDependencias();

        var resultado = service.ejecutar(5L);

        assertThat(resultado.tieneDependencias()).isFalse();
        assertThat(resultado.bloqueado()).isFalse();
        assertThat(resultado.dependencias()).isEmpty();
    }

    @Test
    void ejecutar_conProductoInexistente_lanzaExcepcion() {
        when(productoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
