package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ProductoResult;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.producto.domain.model.TipoVenta;
import com.eldiamante360.producto.domain.model.UnidadMedida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarProductosStockBajoServiceTest {

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    private ListarProductosStockBajoService service;

    @BeforeEach
    void setUp() {
        service = new ListarProductosStockBajoService(productoRepositoryPort);
    }

    @Test
    void ejecutar_retornaLosProductosConStockBajoMapeados() {
        CategoriaProducto categoria = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
        Producto producto = new Producto(5L, "Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.ONE, BigDecimal.valueOf(5), true, 0);
        when(productoRepositoryPort.listarConStockBajo()).thenReturn(List.of(producto));

        List<ProductoResult> resultado = service.ejecutar();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).stockBajo()).isTrue();
    }
}
