package com.eldiamante360.auth.infrastructure.persistence.adapter;

import com.eldiamante360.auth.application.port.RolRepositoryPort;
import com.eldiamante360.auth.domain.model.Rol;
import com.eldiamante360.auth.infrastructure.mapper.RolMapper;
import com.eldiamante360.auth.infrastructure.persistence.repository.RolJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RolRepositoryAdapter implements RolRepositoryPort {

    private final RolJpaRepository rolJpaRepository;
    private final RolMapper rolMapper;

    public RolRepositoryAdapter(RolJpaRepository rolJpaRepository, RolMapper rolMapper) {
        this.rolJpaRepository = rolJpaRepository;
        this.rolMapper = rolMapper;
    }

    @Override
    public Optional<Rol> buscarPorId(Long id) {
        return rolJpaRepository.findById(id).map(rolMapper::toDomain);
    }

    @Override
    public Optional<Rol> buscarPorNombre(String nombre) {
        return rolJpaRepository.findByNombre(nombre).map(rolMapper::toDomain);
    }

    @Override
    public List<Rol> listarTodos() {
        return rolJpaRepository.findAll().stream().map(rolMapper::toDomain).toList();
    }
}
