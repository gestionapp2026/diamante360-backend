package com.eldiamante360.deudor.infrastructure.persistence.adapter;

import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import com.eldiamante360.deudor.infrastructure.mapper.CuentaPorCobrarMapper;
import com.eldiamante360.deudor.infrastructure.persistence.entity.CuentaPorCobrarEntity;
import com.eldiamante360.deudor.infrastructure.persistence.repository.CuentaPorCobrarJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class CuentaPorCobrarRepositoryAdapter implements CuentaPorCobrarRepositoryPort {

    private final CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository;
    private final CuentaPorCobrarMapper cuentaPorCobrarMapper;

    public CuentaPorCobrarRepositoryAdapter(CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository,
                                             CuentaPorCobrarMapper cuentaPorCobrarMapper) {
        this.cuentaPorCobrarJpaRepository = cuentaPorCobrarJpaRepository;
        this.cuentaPorCobrarMapper = cuentaPorCobrarMapper;
    }

    @Override
    public Optional<CuentaPorCobrar> buscarPorId(Long id) {
        return cuentaPorCobrarJpaRepository.findById(id).map(cuentaPorCobrarMapper::toDomain);
    }

    @Override
    public Optional<CuentaPorCobrar> buscarPorFacturaId(Long facturaId) {
        return cuentaPorCobrarJpaRepository.findByFacturaId(facturaId).map(cuentaPorCobrarMapper::toDomain);
    }

    @Override
    public CuentaPorCobrar guardar(CuentaPorCobrar cuentaPorCobrar) {
        CuentaPorCobrarEntity entity = CuentaPorCobrarEntity.builder()
                .id(cuentaPorCobrar.getId())
                .facturaId(cuentaPorCobrar.getFacturaId())
                .numeroFactura(cuentaPorCobrar.getNumeroFactura())
                .clienteId(cuentaPorCobrar.getClienteId())
                .clienteNombre(cuentaPorCobrar.getClienteNombre())
                .clienteNumeroDocumento(cuentaPorCobrar.getClienteNumeroDocumento())
                .montoOriginal(cuentaPorCobrar.getMontoOriginal())
                .saldoPendiente(cuentaPorCobrar.getSaldoPendiente())
                .estado(cuentaPorCobrar.getEstado())
                .usuarioId(cuentaPorCobrar.getUsuarioId())
                .fecha(cuentaPorCobrar.getFecha())
                .fechaUltimoAbono(cuentaPorCobrar.getFechaUltimoAbono())
                .fechaAnulacion(cuentaPorCobrar.getFechaAnulacion())
                .version(cuentaPorCobrar.getVersion())
                .build();

        CuentaPorCobrarEntity guardada = cuentaPorCobrarJpaRepository.save(entity);
        return cuentaPorCobrarMapper.toDomain(guardada);
    }

    @Override
    public Page<CuentaPorCobrar> listar(Pageable pageable) {
        return cuentaPorCobrarJpaRepository.findAll(pageable).map(cuentaPorCobrarMapper::toDomain);
    }

    @Override
    public Page<CuentaPorCobrar> listarPorCliente(Long clienteId, Pageable pageable) {
        return cuentaPorCobrarJpaRepository.findByClienteId(clienteId, pageable).map(cuentaPorCobrarMapper::toDomain);
    }

    @Override
    public Page<CuentaPorCobrar> listarPorEstado(EstadoCuentaPorCobrar estado, Pageable pageable) {
        return cuentaPorCobrarJpaRepository.findByEstado(estado, pageable).map(cuentaPorCobrarMapper::toDomain);
    }

    @Override
    public BigDecimal sumarSaldoPendientePorCliente(Long clienteId) {
        return cuentaPorCobrarJpaRepository.sumarSaldoPendientePorCliente(clienteId);
    }
}
