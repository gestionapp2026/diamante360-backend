package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.RestablecerPasswordResult;
import com.eldiamante360.auth.application.port.PasswordEncoderPort;
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
class RestablecerPasswordServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private RestablecerPasswordService service;

    private Rol rolAdmin;

    @BeforeEach
    void setUp() {
        service = new RestablecerPasswordService(usuarioRepositoryPort, passwordEncoderPort);
        rolAdmin = new Rol(1L, "ADMIN", "Administrador", Set.of());
    }

    @Test
    void ejecutar_generaPasswordTemporalYObligaCambioEnProximoLogin() {
        Usuario usuario = new Usuario(5L, "angie", "hashViejo", "Angie", rolAdmin, true, false, null, 0);
        when(usuarioRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(usuario));
        when(passwordEncoderPort.encode(any())).thenReturn("hashNuevo");
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        RestablecerPasswordResult resultado = service.ejecutar(5L, 1L);

        assertThat(resultado.id()).isEqualTo(5L);
        assertThat(resultado.username()).isEqualTo("angie");
        assertThat(resultado.passwordTemporal()).hasSize(10);
        assertThat(resultado.passwordTemporal()).matches("^(?=.*[A-Za-z])(?=.*\\d).+$");
        assertThat(usuario.isDebeCambiarPassword()).isTrue();
        assertThat(usuario.getPasswordHash()).isEqualTo("hashNuevo");
    }

    @Test
    void ejecutar_intentaRestablecerSuPropiaPassword_lanzaExcepcion() {
        assertThatThrownBy(() -> service.ejecutar(1L, 1L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("propia contrasena");

        verify(usuarioRepositoryPort, never()).buscarPorId(any());
        verify(usuarioRepositoryPort, never()).guardar(any());
    }

    @Test
    void ejecutar_conUsuarioInexistente_lanzaExcepcion() {
        when(usuarioRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, 1L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
