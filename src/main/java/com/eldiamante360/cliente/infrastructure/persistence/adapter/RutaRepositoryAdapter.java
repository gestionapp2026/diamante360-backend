package com.eldiamante360.cliente.infrastructure.persistence.adapter;

import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.domain.model.Ruta;
import com.eldiamante360.cliente.infrastructure.mapper.RutaMapper;
import com.eldiamante360.cliente.infrastructure.persistence.entity.RutaEntity;
import com.eldiamante360.cliente.infrastructure.persistence.repository.RutaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RutaRepositoryAdapter implements RutaRepositoryPort {

    private final RutaJpaRepository rutaJpaRepository;
    private final RutaMapper rutaMapper;

    public RutaRepositoryAdapter(RutaJpaRepository rutaJpaRepository, RutaMapper rutaMapper) {
        this.rutaJpaRepository = rutaJpaRepository;
        this.rutaMapper = rutaMapper;
    }

    @Override
    public Optional<Ruta> buscarPorId(Long id) {
        return rutaJpaRepository.findById(id).map(rutaMapper::toDomain);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return rutaJpaRepository.existsByNombre(nombre);
    }

    @Override
    public Ruta guardar(Ruta ruta) {
        RutaEntity entity = RutaEntity.builder()
                .id(ruta.getId())
                .nombre(ruta.getNombre())
                .descripcion(ruta.getDescripcion())
                .activo(ruta.isActivo())
                .build();

        RutaEntity guardada = rutaJpaRepository.save(entity);
        return rutaMapper.toDomain(guardada);
    }

    @Override
    public List<Ruta> listarTodas() {
        return rutaJpaRepository.findAll().stream()
                .map(rutaMapper::toDomain)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        rutaJpaRepository.deleteById(id);
    }
}
