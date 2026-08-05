package com.eldiamante360.cliente.infrastructure.persistence.adapter;

import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.infrastructure.mapper.ClienteMapper;
import com.eldiamante360.cliente.infrastructure.persistence.entity.ClienteEntity;
import com.eldiamante360.cliente.infrastructure.persistence.entity.RutaEntity;
import com.eldiamante360.cliente.infrastructure.persistence.repository.ClienteJpaRepository;
import com.eldiamante360.cliente.infrastructure.persistence.repository.RutaJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;
    private final RutaJpaRepository rutaJpaRepository;
    private final ClienteMapper clienteMapper;

    public ClienteRepositoryAdapter(ClienteJpaRepository clienteJpaRepository,
                                     RutaJpaRepository rutaJpaRepository,
                                     ClienteMapper clienteMapper) {
        this.clienteJpaRepository = clienteJpaRepository;
        this.rutaJpaRepository = rutaJpaRepository;
        this.clienteMapper = clienteMapper;
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteJpaRepository.findById(id).map(clienteMapper::toDomain);
    }

    @Override
    public boolean existePorNumeroDocumento(String numeroDocumento) {
        return clienteJpaRepository.existsByNumeroDocumento(numeroDocumento);
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        RutaEntity rutaReferencia = cliente.getRuta() != null
                ? rutaJpaRepository.getReferenceById(cliente.getRuta().getId())
                : null;

        ClienteEntity entity = ClienteEntity.builder()
                .id(cliente.getId())
                .tipoDocumento(cliente.getTipoDocumento())
                .numeroDocumento(cliente.getNumeroDocumento())
                .nombre(cliente.getNombre())
                .telefono(cliente.getTelefono())
                .email(cliente.getEmail())
                .direccion(cliente.getDireccion())
                .ruta(rutaReferencia)
                .activo(cliente.isActivo())
                .version(cliente.getVersion())
                .build();

        ClienteEntity guardado = clienteJpaRepository.save(entity);
        return clienteMapper.toDomain(guardado);
    }

    @Override
    public Page<Cliente> listar(Pageable pageable) {
        return clienteJpaRepository.findAll(pageable).map(clienteMapper::toDomain);
    }

    @Override
    public Page<Cliente> buscar(String texto, Pageable pageable) {
        return clienteJpaRepository.buscarPorTexto(texto, pageable).map(clienteMapper::toDomain);
    }

    @Override
    public Page<Cliente> listarPorRuta(Long rutaId, Pageable pageable) {
        return clienteJpaRepository.findByRutaId(rutaId, pageable).map(clienteMapper::toDomain);
    }

    @Override
    public void eliminar(Long id) {
        clienteJpaRepository.deleteById(id);
    }
}
