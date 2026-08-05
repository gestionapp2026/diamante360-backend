package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.CrearProductoCommand;
import com.eldiamante360.producto.application.dto.ProductoResult;
import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.exception.CategoriaInactivaException;
import com.eldiamante360.producto.domain.exception.NombreProductoDuplicadoException;
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
class CrearProductoServiceTest {

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @Mock
    private CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;

    private CrearProductoService service;

    private CategoriaProducto categoriaActiva;

    @BeforeEach
    void setUp() {
        service = new CrearProductoService(productoRepositoryPort, categoriaProductoRepositoryPort);
        categoriaActiva = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
    }

    @Test
    void ejecutar_conDatosValidos_creaElProducto() {
        CrearProductoCommand command = new CrearProductoCommand("Chorizo", 1L, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);
        when(productoRepositoryPort.existePorNombre("Chorizo")).thenReturn(false);
        when(categoriaProductoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(categoriaActiva));
        when(productoRepositoryPort.guardar(any(Producto.class))).thenAnswer(invocacion -> {
            Producto p = invocacion.getArgument(0);
            return new Producto(20L, p.getNombre(), p.getCategoria(), p.getTipoVenta(), p.getUnidadMedida(),
                    p.getPrecioCompra(), p.getPrecioVenta(), p.getStockActual(), p.getStockMinimo(), p.isActivo(), 0);
        });

        ProductoResult resultado = service.ejecutar(command);

        assertThat(resultado.id()).isEqualTo(20L);
        assertThat(resultado.nombre()).isEqualTo("Chorizo");
        assertThat(resultado.categoriaId()).isEqualTo(1L);
    }

    @Test
    void ejecutar_conNombreYaExistente_lanzaExcepcion() {
        CrearProductoCommand command = new CrearProductoCommand("Chorizo", 1L, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);
        when(productoRepositoryPort.existePorNombre("Chorizo")).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(NombreProductoDuplicadoException.class);
    }

    @Test
    void ejecutar_conCategoriaInexistente_lanzaExcepcion() {
        CrearProductoCommand command = new CrearProductoCommand("Chorizo", 99L, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);
        when(productoRepositoryPort.existePorNombre("Chorizo")).thenReturn(false);
        when(categoriaProductoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void ejecutar_conCategoriaInactiva_lanzaExcepcion() {
        CategoriaProducto categoriaInactiva = new CategoriaProducto(2L, "Descontinuados", null, false);
        CrearProductoCommand command = new CrearProductoCommand("Chorizo", 2L, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);
        when(productoRepositoryPort.existePorNombre("Chorizo")).thenReturn(false);
        when(categoriaProductoRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(categoriaInactiva));

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(CategoriaInactivaException.class);
    }
}
