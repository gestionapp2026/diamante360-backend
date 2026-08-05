package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.CategoriaResult;
import com.eldiamante360.producto.application.dto.CrearCategoriaCommand;
import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.domain.exception.NombreCategoriaDuplicadaException;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearCategoriaServiceTest {

    @Mock
    private CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;

    private CrearCategoriaService service;

    @BeforeEach
    void setUp() {
        service = new CrearCategoriaService(categoriaProductoRepositoryPort);
    }

    @Test
    void ejecutar_conNombreLibre_creaLaCategoria() {
        CrearCategoriaCommand command = new CrearCategoriaCommand("Ahumados", "Productos ahumados");
        when(categoriaProductoRepositoryPort.existePorNombre("Ahumados")).thenReturn(false);
        when(categoriaProductoRepositoryPort.guardar(any(CategoriaProducto.class))).thenAnswer(invocacion -> {
            CategoriaProducto c = invocacion.getArgument(0);
            return new CategoriaProducto(1L, c.getNombre(), c.getDescripcion(), c.isActivo());
        });

        CategoriaResult resultado = service.ejecutar(command);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nombre()).isEqualTo("Ahumados");
        assertThat(resultado.activo()).isTrue();
    }

    @Test
    void ejecutar_conNombreYaExistente_lanzaExcepcion() {
        CrearCategoriaCommand command = new CrearCategoriaCommand("Ahumados", "Productos ahumados");
        when(categoriaProductoRepositoryPort.existePorNombre("Ahumados")).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(NombreCategoriaDuplicadaException.class);
    }
}
