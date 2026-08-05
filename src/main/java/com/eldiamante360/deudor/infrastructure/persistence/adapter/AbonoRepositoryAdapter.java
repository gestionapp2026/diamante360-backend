package com.eldiamante360.deudor.infrastructure.persistence.adapter;

import com.eldiamante360.deudor.application.port.AbonoRepositoryPort;
import com.eldiamante360.deudor.domain.model.Abono;
import com.eldiamante360.deudor.infrastructure.mapper.AbonoMapper;
import com.eldiamante360.deudor.infrastructure.persistence.entity.AbonoEntity;
import com.eldiamante360.deudor.infrastructure.persistence.repository.AbonoJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AbonoRepositoryAdapter implements AbonoRepositoryPort {

    private final AbonoJpaRepository abonoJpaRepository;
    private final AbonoMapper abonoMapper;

    public AbonoRepositoryAdapter(AbonoJpaRepository abonoJpaRepository, AbonoMapper abonoMapper) {
        this.abonoJpaRepository = abonoJpaRepository;
        this.abonoMapper = abonoMapper;
    }

    @Override
    public Abono guardar(Abono abono) {
        AbonoEntity entity = AbonoEntity.builder()
                .id(abono.id())
                .cuentaPorCobrarId(abono.cuentaPorCobrarId())
                .monto(abono.monto())
                .usuarioId(abono.usuarioId())
                .fecha(abono.fecha() != null ? abono.fecha() : Instant.now())
                .medioPago(abono.medioPago())
                .build();

        AbonoEntity guardado = abonoJpaRepository.save(entity);
        return abonoMapper.toDomain(guardado);
    }

    @Override
    public Page<Abono> listarPorCuenta(Long cuentaPorCobrarId, Pageable pageable) {
        return abonoJpaRepository.findByCuentaPorCobrarId(cuentaPorCobrarId, pageable).map(abonoMapper::toDomain);
    }
}
