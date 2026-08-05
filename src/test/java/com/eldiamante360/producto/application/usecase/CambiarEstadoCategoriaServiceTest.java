package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.CategoriaResult;
import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoCategoriaServiceTest {

    @Mock
    private CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;

    private CambiarEstadoCategoriaService service;

    @BeforeEach
    void setUp() {
        service = new CambiarEstadoCategoriaService(categoriaProductoRepositoryPort);
    }

    @Test
    void ejecutar_conActivoFalso_desactivaLaCategoria() {
        CategoriaProducto categoria = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
        when(categoriaProductoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(categoria));
        when(categoriaProductoRepositoryPort.guardar(any(CategoriaProducto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        CategoriaResult resultado = service.ejecutar(1L, false);

        assertThat(resultado.activo()).isFalse();
    }

    @Test
    void ejecutar_conIdInexistente_lanzaExcepcion() {
        when(categoriaProductoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, true))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
