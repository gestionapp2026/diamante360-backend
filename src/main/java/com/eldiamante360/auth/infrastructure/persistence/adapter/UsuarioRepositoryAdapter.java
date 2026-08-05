package com.eldiamante360.auth.infrastructure.persistence.adapter;

import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.auth.infrastructure.mapper.UsuarioMapper;
import com.eldiamante360.auth.infrastructure.persistence.entity.RolEntity;
import com.eldiamante360.auth.infrastructure.persistence.entity.UsuarioEntity;
import com.eldiamante360.auth.infrastructure.persistence.repository.RolJpaRepository;
import com.eldiamante360.auth.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final RolJpaRepository rolJpaRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository usuarioJpaRepository,
                                     RolJpaRepository rolJpaRepository,
                                     UsuarioMapper usuarioMapper) {
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.rolJpaRepository = rolJpaRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioJpaRepository.findByUsername(username).map(usuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioJpaRepository.findById(id).map(usuarioMapper::toDomain);
    }

    @Override
    public boolean existePorUsername(String username) {
        return usuarioJpaRepository.existsByUsername(username);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        RolEntity rolReferencia = rolJpaRepository.getReferenceById(usuario.getRol().id());

        UsuarioEntity entity = UsuarioEntity.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .passwordHash(usuario.getPasswordHash())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(rolReferencia)
                .activo(usuario.isActivo())
                .debeCambiarPassword(usuario.isDebeCambiarPassword())
                .ultimoLogin(usuario.getUltimoLogin())
                .version(usuario.getVersion())
                .build();

        UsuarioEntity guardado = usuarioJpaRepository.save(entity);
        return usuarioMapper.toDomain(guardado);
    }

    @Override
    public Page<Usuario> listar(Pageable pageable) {
        return usuarioJpaRepository.findAll(pageable).map(usuarioMapper::toDomain);
    }

    @Override
    public void eliminar(Long id) {
        usuarioJpaRepository.deleteById(id);
    }
}
