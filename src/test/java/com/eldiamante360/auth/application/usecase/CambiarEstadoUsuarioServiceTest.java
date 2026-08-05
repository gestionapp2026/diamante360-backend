package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.UsuarioResult;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.model.Rol;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoUsuarioServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    private CambiarEstadoUsuarioService service;

    private Rol rolAdmin;

    @BeforeEach
    void setUp() {
        service = new CambiarEstadoUsuarioService(usuarioRepositoryPort);
        rolAdmin = new Rol(1L, "ADMIN", "Administrador", Set.of());
    }

    @Test
    void ejecutar_desactivaUnUsuarioDistintoAlSolicitante() {
        Usuario usuario = new Usuario(5L, "angie", "hash", "Angie", rolAdmin, true, false, null, 0);
        when(usuarioRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(usuario));
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResult resultado = service.ejecutar(5L, false, 1L);

        assertThat(resultado.activo()).isFalse();
    }

    @Test
    void ejecutar_intentaDesactivarsePropioUsuario_lanzaExcepcion() {
        assertThatThrownBy(() -> service.ejecutar(1L, false, 1L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("propio usuario");

        verify(usuarioRepositoryPort, never()).buscarPorId(any());
        verify(usuarioRepositoryPort, never()).guardar(any());
    }

    @Test
    void ejecutar_activarsePropioUsuario_siEstaPermitido() {
        Usuario usuario = new Usuario(1L, "jhon", "hash", "Jhon", rolAdmin, false, false, null, 0);
        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResult resultado = service.ejecutar(1L, true, 1L);

        assertThat(resultado.activo()).isTrue();
    }

    @Test
    void ejecutar_conUsuarioInexistente_lanzaExcepcion() {
        when(usuarioRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, false, 1L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
