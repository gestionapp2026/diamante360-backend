package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.CambiarPasswordCommand;
import com.eldiamante360.auth.application.port.PasswordEncoderPort;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.exception.PasswordActualIncorrectaException;
import com.eldiamante360.auth.domain.model.Rol;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarPasswordServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private CambiarPasswordService service;

    private Rol rolAdmin;

    @BeforeEach
    void setUp() {
        service = new CambiarPasswordService(usuarioRepositoryPort, passwordEncoderPort);
        rolAdmin = new Rol(1L, "ADMIN", "Administrador", Set.of());
    }

    @Test
    void ejecutar_conPasswordActualCorrecta_actualizaElHash() {
        Usuario usuario = new Usuario(1L, "jhon", "hashViejo", "Jhon", rolAdmin, true, true, null, 0);
        CambiarPasswordCommand command = new CambiarPasswordCommand(1L, "actual123", "nueva12345");

        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoderPort.matches("actual123", "hashViejo")).thenReturn(true);
        when(passwordEncoderPort.encode("nueva12345")).thenReturn("hashNuevo");

        service.ejecutar(command);

        assertThat(usuario.getPasswordHash()).isEqualTo("hashNuevo");
        assertThat(usuario.isDebeCambiarPassword()).isFalse();
        verify(usuarioRepositoryPort).guardar(usuario);
    }

    @Test
    void ejecutar_conPasswordActualIncorrecta_lanzaExcepcionYNoGuarda() {
        Usuario usuario = new Usuario(1L, "jhon", "hashViejo", "Jhon", rolAdmin, true, true, null, 0);
        CambiarPasswordCommand command = new CambiarPasswordCommand(1L, "incorrecta", "nueva12345");

        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoderPort.matches("incorrecta", "hashViejo")).thenReturn(false);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(PasswordActualIncorrectaException.class);

        verify(usuarioRepositoryPort, never()).guardar(usuario);
    }

    @Test
    void ejecutar_conUsuarioInexistente_lanzaExcepcion() {
        CambiarPasswordCommand command = new CambiarPasswordCommand(404L, "actual123", "nueva12345");
        when(usuarioRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
