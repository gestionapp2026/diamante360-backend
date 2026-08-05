package com.eldiamante360.formula.infrastructure.persistence.adapter;

import com.eldiamante360.formula.application.port.FormulaRepositoryPort;
import com.eldiamante360.formula.domain.model.DetalleFormula;
import com.eldiamante360.formula.domain.model.Formula;
import com.eldiamante360.formula.infrastructure.mapper.FormulaMapper;
import com.eldiamante360.formula.infrastructure.persistence.entity.DetalleFormulaEntity;
import com.eldiamante360.formula.infrastructure.persistence.entity.FormulaEntity;
import com.eldiamante360.formula.infrastructure.persistence.repository.FormulaJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FormulaRepositoryAdapter implements FormulaRepositoryPort {

    private final FormulaJpaRepository formulaJpaRepository;
    private final FormulaMapper formulaMapper;

    public FormulaRepositoryAdapter(FormulaJpaRepository formulaJpaRepository, FormulaMapper formulaMapper) {
        this.formulaJpaRepository = formulaJpaRepository;
        this.formulaMapper = formulaMapper;
    }

    @Override
    public Optional<Formula> buscarPorId(Long id) {
        return formulaJpaRepository.findById(id).map(formulaMapper::toDomain);
    }

    @Override
    public Optional<Formula> buscarPorProductoId(Long productoId) {
        return formulaJpaRepository.findByProductoId(productoId).map(formulaMapper::toDomain);
    }

    @Override
    public boolean existePorProductoId(Long productoId) {
        return formulaJpaRepository.existsByProductoId(productoId);
    }

    @Override
    public Formula guardar(Formula formula) {
        FormulaEntity entity = FormulaEntity.builder()
                .id(formula.getId())
                .productoId(formula.getProductoId())
                .productoNombre(formula.getProductoNombre())
                .cantidadBase(formula.getCantidadBase())
                .unidadBase(formula.getUnidadBase())
                .activo(formula.isActivo())
                .version(formula.getVersion())
                .build();

        for (DetalleFormula detalle : formula.getDetalles()) {
            entity.agregarDetalle(DetalleFormulaEntity.builder()
                    .id(detalle.id())
                    .insumoId(detalle.insumoId())
                    .numero(detalle.numero())
                    .cantidad(detalle.cantidad())
                    .build());
        }

        FormulaEntity guardado = formulaJpaRepository.save(entity);
        return formulaMapper.toDomain(guardado);
    }

    @Override
    public Page<Formula> listar(Pageable pageable) {
        return formulaJpaRepository.findAll(pageable).map(formulaMapper::toDomain);
    }

    @Override
    public void eliminar(Long id) {
        formulaJpaRepository.deleteById(id);
    }
}
