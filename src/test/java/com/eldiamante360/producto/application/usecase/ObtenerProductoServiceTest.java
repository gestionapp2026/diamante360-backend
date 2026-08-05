package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ProductoResult;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerProductoServiceTest {

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    private ObtenerProductoService service;

    @BeforeEach
    void setUp() {
        service = new ObtenerProductoService(productoRepositoryPort);
    }

    @Test
    void ejecutar_conIdExistente_retornaElProducto() {
        CategoriaProducto categoria = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
        Producto producto = new Producto(5L, "Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE, true, 0);
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(producto));

        ProductoResult resultado = service.ejecutar(5L);

        assertThat(resultado.id()).isEqualTo(5L);
        assertThat(resultado.nombre()).isEqualTo("Chorizo");
    }

    @Test
    void ejecutar_conIdInexistente_lanzaExcepcion() {
        when(productoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
