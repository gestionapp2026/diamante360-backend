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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarProductosServiceTest {

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    private ListarProductosService service;

    @BeforeEach
    void setUp() {
        service = new ListarProductosService(productoRepositoryPort);
    }

    @Test
    void ejecutar_retornaLaPaginaMapeada() {
        CategoriaProducto categoria = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
        Producto producto = new Producto(5L, "Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE, true, 0);
        Pageable pageable = PageRequest.of(0, 10);
        when(productoRepositoryPort.listar(pageable)).thenReturn(new PageImpl<>(List.of(producto)));

        var resultado = service.ejecutar(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0)).isInstanceOf(ProductoResult.class);
        assertThat(resultado.getContent().get(0).nombre()).isEqualTo("Chorizo");
    }
}
