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
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarProductoServiceTest {

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

    private EliminarProductoService service;

    private Producto producto;

    @BeforeEach
    void setUp() {
        service = new EliminarProductoService(productoRepositoryPort, detalleFacturaJpaRepository,
                detalleOrdenJpaRepository, movimientoInventarioJpaRepository, formulaJpaRepository);
        CategoriaProducto categoria = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
        producto = new Producto(5L, "Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE, true, 0);
    }

    private void sinDependencias() {
        lenient().when(detalleFacturaJpaRepository.existsByProductoId(5L)).thenReturn(false);
        lenient().when(detalleOrdenJpaRepository.existsByProductoId(5L)).thenReturn(false);
        lenient().when(movimientoInventarioJpaRepository.existsByProductoId(5L)).thenReturn(false);
        lenient().when(formulaJpaRepository.existsByProductoId(5L)).thenReturn(false);
    }

    @Test
    void ejecutar_sinDependencias_loElimina() {
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        sinDependencias();

        service.ejecutar(5L, false);

        verify(productoRepositoryPort).eliminar(5L);
    }

    @Test
    void ejecutar_conFacturasAsociadas_lanzaExcepcion() {
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(detalleFacturaJpaRepository.existsByProductoId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(5L, false))
                .isInstanceOf(RecursoConDependenciasException.class);

        verify(productoRepositoryPort, never()).eliminar(5L);
    }

    @Test
    void ejecutar_conOrdenesAsociadas_lanzaExcepcion() {
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(detalleFacturaJpaRepository.existsByProductoId(5L)).thenReturn(false);
        when(detalleOrdenJpaRepository.existsByProductoId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(5L, false))
                .isInstanceOf(RecursoConDependenciasException.class);

        verify(productoRepositoryPort, never()).eliminar(5L);
    }

    @Test
    void ejecutar_conMovimientosDeInventarioAsociados_lanzaExcepcion() {
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(detalleFacturaJpaRepository.existsByProductoId(5L)).thenReturn(false);
        when(detalleOrdenJpaRepository.existsByProductoId(5L)).thenReturn(false);
        when(movimientoInventarioJpaRepository.existsByProductoId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(5L, false))
                .isInstanceOf(RecursoConDependenciasException.class);

        verify(productoRepositoryPort, never()).eliminar(5L);
    }

    @Test
    void ejecutar_conFormulaAsociada_lanzaExcepcion() {
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(detalleFacturaJpaRepository.existsByProductoId(5L)).thenReturn(false);
        when(detalleOrdenJpaRepository.existsByProductoId(5L)).thenReturn(false);
        when(movimientoInventarioJpaRepository.existsByProductoId(5L)).thenReturn(false);
        when(formulaJpaRepository.existsByProductoId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(5L, false))
                .isInstanceOf(RecursoConDependenciasException.class);

        verify(productoRepositoryPort, never()).eliminar(5L);
    }

    @Test
    void ejecutar_conProductoInexistente_lanzaExcepcion() {
        when(productoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, false))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(productoRepositoryPort, never()).eliminar(404L);
    }

    @Test
    void ejecutar_conCascadaTrueYMovimientosYFormula_losBorraYEliminaProducto() {
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(detalleFacturaJpaRepository.existsByProductoId(5L)).thenReturn(false);
        when(detalleOrdenJpaRepository.existsByProductoId(5L)).thenReturn(false);

        service.ejecutar(5L, true);

        verify(movimientoInventarioJpaRepository).deleteByProductoId(5L);
        verify(formulaJpaRepository).deleteByProductoId(5L);
        verify(productoRepositoryPort).eliminar(5L);
    }

    @Test
    void ejecutar_conCascadaTrueYFacturasAsociadas_sigueBloqueando() {
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(detalleFacturaJpaRepository.existsByProductoId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(5L, true))
                .isInstanceOf(RecursoConDependenciasException.class)
                .hasMessage(EliminarProductoService.MENSAJE_BLOQUEO_NO_CASCADEABLE);

        verify(productoRepositoryPort, never()).eliminar(5L);
        verify(movimientoInventarioJpaRepository, never()).deleteByProductoId(5L);
        verify(formulaJpaRepository, never()).deleteByProductoId(5L);
    }

    @Test
    void ejecutar_conCascadaTrueYOrdenesAsociadas_sigueBloqueando() {
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(detalleFacturaJpaRepository.existsByProductoId(5L)).thenReturn(false);
        when(detalleOrdenJpaRepository.existsByProductoId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(5L, true))
                .isInstanceOf(RecursoConDependenciasException.class)
                .hasMessage(EliminarProductoService.MENSAJE_BLOQUEO_NO_CASCADEABLE);

        verify(productoRepositoryPort, never()).eliminar(5L);
    }
}
