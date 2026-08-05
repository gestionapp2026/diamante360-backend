package com.eldiamante360.insumoquimico.infrastructure.persistence.adapter;

import com.eldiamante360.insumoquimico.application.port.LoteInsumoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.LoteInsumo;
import com.eldiamante360.insumoquimico.infrastructure.mapper.LoteInsumoMapper;
import com.eldiamante360.insumoquimico.infrastructure.persistence.entity.LoteInsumoEntity;
import com.eldiamante360.insumoquimico.infrastructure.persistence.repository.LoteInsumoJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class LoteInsumoRepositoryAdapter implements LoteInsumoRepositoryPort {

    private final LoteInsumoJpaRepository loteInsumoJpaRepository;
    private final LoteInsumoMapper loteInsumoMapper;

    public LoteInsumoRepositoryAdapter(LoteInsumoJpaRepository loteInsumoJpaRepository,
                                        LoteInsumoMapper loteInsumoMapper) {
        this.loteInsumoJpaRepository = loteInsumoJpaRepository;
        this.loteInsumoMapper = loteInsumoMapper;
    }

    @Override
    public Optional<LoteInsumo> buscarPorId(Long id) {
        return loteInsumoJpaRepository.findById(id).map(loteInsumoMapper::toDomain);
    }

    @Override
    public LoteInsumo guardar(LoteInsumo lote) {
        LoteInsumoEntity entity = LoteInsumoEntity.builder()
                .id(lote.getId())
                .insumoId(lote.getInsumoId())
                .numeroLote(lote.getNumeroLote())
                .fechaVencimiento(lote.getFechaVencimiento())
                .cantidadActual(lote.getCantidadActual())
                .fechaIngreso(lote.getFechaIngreso() != null ? lote.getFechaIngreso() : Instant.now())
                .version(lote.getVersion())
                .build();

        LoteInsumoEntity guardado = loteInsumoJpaRepository.save(entity);
        return loteInsumoMapper.toDomain(guardado);
    }

    @Override
    public Page<LoteInsumo> listarPorInsumo(Long insumoId, Pageable pageable) {
        return loteInsumoJpaRepository.findByInsumoId(insumoId, pageable).map(loteInsumoMapper::toDomain);
    }

    @Override
    public List<LoteInsumo> listarPorVencer(LocalDate hasta) {
        return loteInsumoJpaRepository.findPorVencer(hasta).stream()
                .map(loteInsumoMapper::toDomain)
                .toList();
    }

    @Override
    public List<LoteInsumo> listarDisponiblesFefo(Long insumoId) {
        return loteInsumoJpaRepository.findDisponiblesFefo(insumoId).stream()
                .map(loteInsumoMapper::toDomain)
                .toList();
    }
}
