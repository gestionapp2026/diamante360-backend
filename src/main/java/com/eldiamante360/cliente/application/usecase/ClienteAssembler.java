package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.dto.HistorialClienteResult;
import com.eldiamante360.cliente.application.dto.ObservacionClienteResult;
import com.eldiamante360.cliente.application.dto.PrecioClienteProductoResult;
import com.eldiamante360.cliente.application.dto.RutaResult;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.HistorialCliente;
import com.eldiamante360.cliente.domain.model.ObservacionCliente;
import com.eldiamante360.cliente.domain.model.PrecioClienteProducto;
import com.eldiamante360.cliente.domain.model.Ruta;

/**
 * Ensambla los DTOs de salida de aplicacion a partir del modelo de dominio.
 * No es un mapper de infraestructura: no conoce JPA ni MapStruct.
 */
final class ClienteAssembler {

    private ClienteAssembler() {
    }

    static ClienteResult toResult(Cliente cliente) {
        Ruta ruta = cliente.getRuta();
        return new ClienteResult(
                cliente.getId(),
                cliente.getTipoDocumento(),
                cliente.getNumeroDocumento(),
                cliente.getNombre(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.getDireccion(),
                ruta != null ? ruta.getId() : null,
                ruta != null ? ruta.getNombre() : null,
                cliente.isActivo()
        );
    }

    static RutaResult toResult(Ruta ruta) {
        return new RutaResult(
                ruta.getId(),
                ruta.getNombre(),
                ruta.getDescripcion(),
                ruta.isActivo()
        );
    }

    static ObservacionClienteResult toResult(ObservacionCliente observacion) {
        return new ObservacionClienteResult(
                observacion.id(),
                observacion.clienteId(),
                observacion.texto(),
                observacion.usuarioId(),
                observacion.fecha()
        );
    }

    static PrecioClienteProductoResult toResult(PrecioClienteProducto precio, String productoNombre) {
        return new PrecioClienteProductoResult(
                precio.getId(),
                precio.getClienteId(),
                precio.getProductoId(),
                productoNombre,
                precio.getPrecio()
        );
    }

    static HistorialClienteResult toResult(HistorialCliente historial) {
        return new HistorialClienteResult(
                historial.id(),
                historial.clienteId(),
                historial.tipoEvento(),
                historial.descripcion(),
                historial.usuarioId(),
                historial.fecha()
        );
    }
}
