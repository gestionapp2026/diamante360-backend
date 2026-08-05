package com.eldiamante360.auth.application.port;

import com.eldiamante360.auth.domain.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Puerto de persistencia para Usuario. La implementacion vive en
 * infrastructure/persistence/adapter y traduce hacia/desde JPA.
 */
public interface UsuarioRepositoryPort {

    Optional<Usuario> buscarPorUsername(String username);

    Optional<Usuario> buscarPorId(Long id);

    boolean existePorUsername(String username);

    Usuario guardar(Usuario usuario);

    Page<Usuario> listar(Pageable pageable);

    void eliminar(Long id);
}
