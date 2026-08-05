package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.producto.infrastructure.persistence.repository.ProductoJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarCategoriaServiceTest {

    @Mock
    private CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;

    @Mock
    private ProductoJpaRepository productoJpaRepository;

    private EliminarCategoriaService service;

    private CategoriaProducto categoria;

    @BeforeEach
    void setUp() {
        service = new EliminarCategoriaService(categoriaProductoRepositoryPort, productoJpaRepository);
        categoria = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
    }

    @Test
    void ejecutar_conCategoriaSinProductos_laElimina() {
        when(categoriaProductoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(categoria));
        when(productoJpaRepository.existsByCategoriaId(1L)).thenReturn(false);

        service.ejecutar(1L, false);

        verify(categoriaProductoRepositoryPort).eliminar(1L);
    }

    @Test
    void ejecutar_conProductosAsociados_lanzaExcepcionYNoElimina() {
        when(categoriaProductoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(categoria));
        when(productoJpaRepository.existsByCategoriaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(1L, false))
                .isInstanceOf(RecursoConDependenciasException.class);

        verify(categoriaProductoRepositoryPort, never()).eliminar(1L);
    }

    @Test
    void ejecutar_conCategoriaInexistente_lanzaExcepcion() {
        when(categoriaProductoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, false))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(categoriaProductoRepositoryPort, never()).eliminar(404L);
    }

    @Test
    void ejecutar_conCascadaTrueYProductosAsociados_siguebloqueando() {
        when(categoriaProductoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(categoria));
        when(productoJpaRepository.existsByCategoriaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(1L, true))
                .isInstanceOf(RecursoConDependenciasException.class);

        verify(categoriaProductoRepositoryPort, never()).eliminar(1L);
    }
}
