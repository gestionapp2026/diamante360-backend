package com.eldiamante360.producto.presentation.mapper;

import com.eldiamante360.producto.application.dto.ActualizarProductoCommand;
import com.eldiamante360.producto.application.dto.CrearProductoCommand;
import com.eldiamante360.producto.application.dto.ProductoResult;
import com.eldiamante360.producto.domain.model.TipoVenta;
import com.eldiamante360.producto.domain.model.UnidadMedida;
import com.eldiamante360.producto.presentation.dto.request.ActualizarProductoRequest;
import com.eldiamante360.producto.presentation.dto.request.CrearProductoRequest;
import com.eldiamante360.producto.presentation.dto.response.ProductoResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductoWebMapperTest {

    private final ProductoWebMapper mapper = Mappers.getMapper(ProductoWebMapper.class);

    @Test
    void toCommand_desdeCrearProductoRequest_mapeaTodosLosCampos() {
        CrearProductoRequest request = new CrearProductoRequest("Chorizo", 1L, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);

        CrearProductoCommand command = mapper.toCommand(request);

        assertThat(command.nombre()).isEqualTo("Chorizo");
        assertThat(command.categoriaId()).isEqualTo(1L);
        assertThat(command.tipoVenta()).isEqualTo(TipoVenta.UNIDAD);
        assertThat(command.unidadMedida()).isEqualTo(UnidadMedida.UND);
        assertThat(command.stockInicial()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void toCommand_desdeIdYActualizarProductoRequest_incluyeElId() {
        ActualizarProductoRequest request = new ActualizarProductoRequest("Chorizo especial", 2L,
                BigDecimal.valueOf(1100), BigDecimal.valueOf(1600), BigDecimal.valueOf(3));

        ActualizarProductoCommand command = mapper.toCommand(7L, request);

        assertThat(command.productoId()).isEqualTo(7L);
        assertThat(command.nombre()).isEqualTo("Chorizo especial");
        assertThat(command.categoriaId()).isEqualTo(2L);
    }

    @Test
    void toResponse_desdeProductoResult_mapeaTodosLosCampos() {
        ProductoResult result = new ProductoResult(5L, "Chorizo", 1L, "Ahumados", TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE, false, true);

        ProductoResponse response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.nombre()).isEqualTo("Chorizo");
        assertThat(response.categoriaNombre()).isEqualTo("Ahumados");
        assertThat(response.stockBajo()).isFalse();
        assertThat(response.activo()).isTrue();
    }
}
