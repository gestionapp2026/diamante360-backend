package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.ActualizarCategoriaCommand;
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
class ActualizarCategoriaServiceTest {

    @Mock
    private CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;

    private ActualizarCategoriaService service;

    @BeforeEach
    void setUp() {
        service = new ActualizarCategoriaService(categoriaProductoRepositoryPort);
    }

    @Test
    void ejecutar_conIdExistente_actualizaLaCategoria() {
        CategoriaProducto categoria = new CategoriaProducto(1L, "Ahumados", "Productos ahumados", true);
        ActualizarCategoriaCommand command = new ActualizarCategoriaCommand(1L, "Ahumados premium", "Linea premium");
        when(categoriaProductoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(categoria));
        when(categoriaProductoRepositoryPort.guardar(any(CategoriaProducto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        CategoriaResult resultado = service.ejecutar(command);

        assertThat(resultado.nombre()).isEqualTo("Ahumados premium");
        assertThat(resultado.descripcion()).isEqualTo("Linea premium");
    }

    @Test
    void ejecutar_conIdInexistente_lanzaExcepcion() {
        ActualizarCategoriaCommand command = new ActualizarCategoriaCommand(404L, "Ahumados", "Productos ahumados");
        when(categoriaProductoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
