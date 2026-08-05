package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ActualizarProductoCommand;
import com.eldiamante360.producto.application.dto.ProductoResult;
import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.exception.CategoriaInactivaException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarProductoServiceTest {

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @Mock
    private CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;

    private ActualizarProductoService service;

    private CategoriaProducto categoriaActiva;
    private Producto producto;

    @BeforeEach
    void setUp() {
        service = new ActualizarProductoService(productoRepositoryPort, categoriaProductoRepositoryPort);
        categoriaActiva = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
        producto = new Producto(5L, "Chorizo", categoriaActiva, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE, true, 0);
    }

    @Test
    void ejecutar_conDatosValidos_actualizaElProducto() {
        ActualizarProductoCommand command = new ActualizarProductoCommand(5L, "Chorizo especial", 1L,
                BigDecimal.valueOf(1100), BigDecimal.valueOf(1600), BigDecimal.valueOf(2));
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(categoriaProductoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(categoriaActiva));
        when(productoRepositoryPort.guardar(any(Producto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        ProductoResult resultado = service.ejecutar(command);

        assertThat(resultado.nombre()).isEqualTo("Chorizo especial");
        assertThat(resultado.precioCompra()).isEqualByComparingTo(BigDecimal.valueOf(1100));
    }

    @Test
    void ejecutar_conProductoInexistente_lanzaExcepcion() {
        ActualizarProductoCommand command = new ActualizarProductoCommand(404L, "Chorizo", 1L,
                BigDecimal.valueOf(1100), BigDecimal.valueOf(1600), BigDecimal.valueOf(2));
        when(productoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void ejecutar_conCategoriaInactiva_lanzaExcepcion() {
        CategoriaProducto categoriaInactiva = new CategoriaProducto(2L, "Descontinuados", null, false);
        ActualizarProductoCommand command = new ActualizarProductoCommand(5L, "Chorizo", 2L,
                BigDecimal.valueOf(1100), BigDecimal.valueOf(1600), BigDecimal.valueOf(2));
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));
        when(categoriaProductoRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(categoriaInactiva));

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(CategoriaInactivaException.class);
    }
}
