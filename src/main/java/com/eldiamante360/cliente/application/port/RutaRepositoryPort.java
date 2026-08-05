package com.eldiamante360.cliente.application.port;

import com.eldiamante360.cliente.domain.model.Ruta;

import java.util.List;
import java.util.Optional;

public interface RutaRepositoryPort {

    Optional<Ruta> buscarPorId(Long id);

    boolean existePorNombre(String nombre);

    Ruta guardar(Ruta ruta);

    List<Ruta> listarTodas();

    void eliminar(Long id);
}
