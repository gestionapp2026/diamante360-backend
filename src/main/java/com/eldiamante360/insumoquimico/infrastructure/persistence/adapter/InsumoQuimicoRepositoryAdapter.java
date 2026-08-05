package com.eldiamante360.insumoquimico.infrastructure.persistence.adapter;

import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.insumoquimico.infrastructure.mapper.InsumoQuimicoMapper;
import com.eldiamante360.insumoquimico.infrastructure.persistence.entity.InsumoQuimicoEntity;
import com.eldiamante360.insumoquimico.infrastructure.persistence.repository.InsumoQuimicoJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InsumoQuimicoRepositoryAdapter implements InsumoQuimicoRepositoryPort {

    private final InsumoQuimicoJpaRepository insumoQuimicoJpaRepository;
    private final InsumoQuimicoMapper insumoQuimicoMapper;

    public InsumoQuimicoRepositoryAdapter(InsumoQuimicoJpaRepository insumoQuimicoJpaRepository,
                                           InsumoQuimicoMapper insumoQuimicoMapper) {
        this.insumoQuimicoJpaRepository = insumoQuimicoJpaRepository;
        this.insumoQuimicoMapper = insumoQuimicoMapper;
    }

    @Override
    public Optional<InsumoQuimico> buscarPorId(Long id) {
        return insumoQuimicoJpaRepository.findById(id).map(insumoQuimicoMapper::toDomain);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return insumoQuimicoJpaRepository.existsByNombre(nombre);
    }

    @Override
    public InsumoQuimico guardar(InsumoQuimico insumoQuimico) {
        InsumoQuimicoEntity entity = InsumoQuimicoEntity.builder()
                .id(insumoQuimico.getId())
                .nombre(insumoQuimico.getNombre())
                .unidadMedida(insumoQuimico.getUnidadMedida())
                .stockActual(insumoQuimico.getStockActual())
                .activo(insumoQuimico.isActivo())
                .version(insumoQuimico.getVersion())
                .precioCompra(insumoQuimico.getPrecioCompra())
                .build();

        InsumoQuimicoEntity guardado = insumoQuimicoJpaRepository.save(entity);
        return insumoQuimicoMapper.toDomain(guardado);
    }

    @Override
    public Page<InsumoQuimico> listar(Pageable pageable) {
        return insumoQuimicoJpaRepository.findAll(pageable).map(insumoQuimicoMapper::toDomain);
    }

    @Override
    public void eliminar(Long id) {
        insumoQuimicoJpaRepository.deleteById(id);
    }
}
