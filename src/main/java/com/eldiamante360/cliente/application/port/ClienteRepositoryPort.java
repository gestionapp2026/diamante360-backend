package com.eldiamante360.cliente.application.port;

import com.eldiamante360.cliente.domain.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClienteRepositoryPort {

    Optional<Cliente> buscarPorId(Long id);

    boolean existePorNumeroDocumento(String numeroDocumento);

    Cliente guardar(Cliente cliente);

    Page<Cliente> listar(Pageable pageable);

    Page<Cliente> buscar(String texto, Pageable pageable);

    Page<Cliente> listarPorRuta(Long rutaId, Pageable pageable);

    void eliminar(Long id);
}
