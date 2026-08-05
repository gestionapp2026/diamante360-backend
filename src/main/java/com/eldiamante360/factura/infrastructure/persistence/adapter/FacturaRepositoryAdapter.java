package com.eldiamante360.factura.infrastructure.persistence.adapter;

import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.factura.domain.model.DetalleFactura;
import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.domain.model.Factura;
import com.eldiamante360.factura.infrastructure.mapper.FacturaMapper;
import com.eldiamante360.factura.infrastructure.persistence.entity.DetalleFacturaEntity;
import com.eldiamante360.factura.infrastructure.persistence.entity.FacturaEntity;
import com.eldiamante360.factura.infrastructure.persistence.repository.FacturaJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.Optional;

@Component
public class FacturaRepositoryAdapter implements FacturaRepositoryPort {

    private final FacturaJpaRepository facturaJpaRepository;
    private final FacturaMapper facturaMapper;

    public FacturaRepositoryAdapter(FacturaJpaRepository facturaJpaRepository, FacturaMapper facturaMapper) {
        this.facturaJpaRepository = facturaJpaRepository;
        this.facturaMapper = facturaMapper;
    }

    @Override
    public Optional<Factura> buscarPorId(Long id) {
        return facturaJpaRepository.findById(id).map(facturaMapper::toDomain);
    }

    @Override
    public Factura guardar(Factura factura) {
        FacturaEntity entity = FacturaEntity.builder()
                .id(factura.getId())
                .numero(factura.getNumero())
                .clienteId(factura.getClienteId())
                .clienteNombre(factura.getClienteNombre())
                .clienteNumeroDocumento(factura.getClienteNumeroDocumento())
                .tipoPago(factura.getTipoPago())
                .estado(factura.getEstado())
                .subtotal(factura.getSubtotal())
                .descuento(factura.getDescuento())
                .total(factura.getTotal())
                .usuarioId(factura.getUsuarioId())
                .usuarioNombre(factura.getUsuarioNombre())
                .fecha(factura.getFecha())
                .fechaAnulacion(factura.getFechaAnulacion())
                .version(factura.getVersion())
                .medioPago(factura.getMedioPago())
                .build();

        for (DetalleFactura detalle : factura.getDetalles()) {
            entity.agregarDetalle(DetalleFacturaEntity.builder()
                    .id(detalle.id())
                    .productoId(detalle.productoId())
                    .productoNombre(detalle.productoNombre())
                    .cantidad(detalle.cantidad())
                    .precioUnitario(detalle.precioUnitario())
                    .porcentajeDescuento(detalle.porcentajeDescuento())
                    .subtotal(detalle.subtotal())
                    .descuento(detalle.descuento())
                    .total(detalle.total())
                    .build());
        }

        FacturaEntity guardado = facturaJpaRepository.save(entity);
        return facturaMapper.toDomain(guardado);
    }

    @Override
    public Page<Factura> listar(Pageable pageable) {
        return facturaJpaRepository.findAll(pageable).map(facturaMapper::toDomain);
    }

    @Override
    public Page<Factura> listarPorCliente(Long clienteId, Pageable pageable) {
        return facturaJpaRepository.findByClienteId(clienteId, pageable).map(facturaMapper::toDomain);
    }

    @Override
    public Page<Factura> listarPorEstado(EstadoFactura estado, Pageable pageable) {
        return facturaJpaRepository.findByEstado(estado, pageable).map(facturaMapper::toDomain);
    }

    @Override
    public String siguienteNumero() {
        Long secuencia = facturaJpaRepository.siguienteNumeroSecuencia();
        return "FAC-%d-%05d".formatted(Year.now().getValue(), secuencia);
    }

    @Override
    public void eliminar(Long id) {
        facturaJpaRepository.deleteById(id);
    }
}
