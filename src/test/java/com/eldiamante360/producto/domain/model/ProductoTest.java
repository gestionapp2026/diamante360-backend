package com.eldiamante360.producto.domain.model;

import com.eldiamante360.producto.domain.exception.CombinacionTipoVentaInvalidaException;
import com.eldiamante360.producto.domain.exception.StockInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductoTest {

    private final CategoriaProducto categoria = CategoriaProducto.nueva("Ahumados", "Productos ahumados");

    @Test
    void nuevo_conTipoUnidadYUnidadUnd_creaProductoActivo() {
        Producto producto = Producto.nuevo("Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);

        assertThat(producto.isActivo()).isTrue();
        assertThat(producto.getId()).isNull();
        assertThat(producto.getStockActual()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void nuevo_conTipoUnidadYUnidadDistintaDeUnd_lanzaExcepcion() {
        assertThatThrownBy(() -> Producto.nuevo("Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.KG,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE))
                .isInstanceOf(CombinacionTipoVentaInvalidaException.class);
    }

    @Test
    void nuevo_conTipoPesoVariableYUnidadUnd_lanzaExcepcion() {
        assertThatThrownBy(() -> Producto.nuevo("Costilla", categoria, TipoVenta.PESO_VARIABLE, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE))
                .isInstanceOf(CombinacionTipoVentaInvalidaException.class);
    }

    @Test
    void nuevo_conTipoPesoVariableYUnidadKgOLb_noLanzaExcepcion() {
        assertThatCode(() -> Producto.nuevo("Costilla", categoria, TipoVenta.PESO_VARIABLE, UnidadMedida.KG,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE))
                .doesNotThrowAnyException();
        assertThatCode(() -> Producto.nuevo("Costilla", categoria, TipoVenta.PESO_VARIABLE, UnidadMedida.LB,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE))
                .doesNotThrowAnyException();
    }

    @Test
    void registrarEntrada_sumaAlStockActual() {
        Producto producto = Producto.nuevo("Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);

        producto.registrarEntrada(BigDecimal.valueOf(5));

        assertThat(producto.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(15));
    }

    @Test
    void registrarSalida_conStockSuficiente_restaDelStockActual() {
        Producto producto = Producto.nuevo("Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);

        producto.registrarSalida(BigDecimal.valueOf(4));

        assertThat(producto.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(6));
    }

    @Test
    void registrarSalida_conStockInsuficiente_lanzaExcepcion() {
        Producto producto = Producto.nuevo("Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);

        assertThatThrownBy(() -> producto.registrarSalida(BigDecimal.valueOf(20)))
                .isInstanceOf(StockInsuficienteException.class);
        assertThat(producto.getStockActual()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void ajustarStock_estableceElValorAbsoluto() {
        Producto producto = Producto.nuevo("Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);

        producto.ajustarStock(BigDecimal.valueOf(3));

        assertThat(producto.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(3));
    }

    @Test
    void tieneStockBajo_conStockMenorOIgualAlMinimo_retornaTrue() {
        Producto producto = Producto.nuevo("Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.valueOf(2), BigDecimal.valueOf(2));

        assertThat(producto.tieneStockBajo()).isTrue();
    }

    @Test
    void tieneStockBajo_conStockMayorAlMinimo_retornaFalse() {
        Producto producto = Producto.nuevo("Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);

        assertThat(producto.tieneStockBajo()).isFalse();
    }

    @Test
    void activarYDesactivar_cambianElEstado() {
        Producto producto = Producto.nuevo("Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);

        producto.desactivar();
        assertThat(producto.isActivo()).isFalse();

        producto.activar();
        assertThat(producto.isActivo()).isTrue();
    }

    @Test
    void actualizarDatos_cambiaCamposEditables() {
        Producto producto = Producto.nuevo("Chorizo", categoria, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);
        CategoriaProducto otraCategoria = CategoriaProducto.nueva("Embutidos", "Productos embutidos");

        producto.actualizarDatos("Chorizo especial", otraCategoria, BigDecimal.valueOf(1100),
                BigDecimal.valueOf(1600), BigDecimal.valueOf(3));

        assertThat(producto.getNombre()).isEqualTo("Chorizo especial");
        assertThat(producto.getCategoria()).isEqualTo(otraCategoria);
        assertThat(producto.getPrecioCompra()).isEqualByComparingTo(BigDecimal.valueOf(1100));
        assertThat(producto.getPrecioVenta()).isEqualByComparingTo(BigDecimal.valueOf(1600));
        assertThat(producto.getStockMinimo()).isEqualByComparingTo(BigDecimal.valueOf(3));
    }
}
