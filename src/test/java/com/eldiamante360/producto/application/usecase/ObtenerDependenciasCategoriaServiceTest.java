package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.producto.infrastructure.persistence.repository.ProductoJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerDependenciasCategoriaServiceTest {

    @Mock
    private CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;

    @Mock
    private ProductoJpaRepository productoJpaRepository;

    private ObtenerDependenciasCategoriaService service;

    private CategoriaProducto categoria;

    @BeforeEach
    void setUp() {
        service = new ObtenerDependenciasCategoriaService(categoriaProductoRepositoryPort, productoJpaRepository);
        categoria = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
    }

    @Test
    void ejecutar_conProductosAsociados_devuelveBloqueadoConConteo() {
        when(categoriaProductoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(categoria));
        when(productoJpaRepository.countByCategoriaId(1L)).thenReturn(3L);

        var resultado = service.ejecutar(1L);

        assertThat(resultado.tieneDependencias()).isTrue();
        assertThat(resultado.bloqueado()).isTrue();
        assertThat(resultado.mensajeBloqueo()).isEqualTo("No se puede eliminar: existen productos en esta categoria.");
        assertThat(resultado.dependencias()).hasSize(1);
        assertThat(resultado.dependencias().get(0).tipo()).isEqualTo("productos");
        assertThat(resultado.dependencias().get(0).cantidad()).isEqualTo(3L);
    }

    @Test
    void ejecutar_sinProductosAsociados_devuelveSinBloqueo() {
        when(categoriaProductoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(categoria));
        when(productoJpaRepository.countByCategoriaId(1L)).thenReturn(0L);

        var resultado = service.ejecutar(1L);

        assertThat(resultado.tieneDependencias()).isFalse();
        assertThat(resultado.bloqueado()).isFalse();
        assertThat(resultado.mensajeBloqueo()).isNull();
        assertThat(resultado.dependencias()).isEmpty();
    }

    @Test
    void ejecutar_conCategoriaInexistente_lanzaExcepcion() {
        when(categoriaProductoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
