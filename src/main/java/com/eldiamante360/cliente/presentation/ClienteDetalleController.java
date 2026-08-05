package com.eldiamante360.cliente.presentation;

import com.eldiamante360.auth.application.usecase.ObtenerSesionActualUseCase;
import com.eldiamante360.cliente.application.usecase.EliminarPrecioClienteProductoUseCase;
import com.eldiamante360.cliente.application.usecase.EstablecerPrecioClienteProductoUseCase;
import com.eldiamante360.cliente.application.usecase.ListarHistorialClienteUseCase;
import com.eldiamante360.cliente.application.usecase.ListarObservacionesClienteUseCase;
import com.eldiamante360.cliente.application.usecase.ListarPreciosClienteUseCase;
import com.eldiamante360.cliente.application.usecase.RegistrarObservacionClienteUseCase;
import com.eldiamante360.cliente.presentation.dto.request.EstablecerPrecioClienteProductoRequest;
import com.eldiamante360.cliente.presentation.dto.request.RegistrarObservacionRequest;
import com.eldiamante360.cliente.presentation.dto.response.HistorialClienteResponse;
import com.eldiamante360.cliente.presentation.dto.response.ObservacionClienteResponse;
import com.eldiamante360.cliente.presentation.dto.response.PrecioClienteProductoResponse;
import com.eldiamante360.cliente.presentation.mapper.HistorialClienteWebMapper;
import com.eldiamante360.cliente.presentation.mapper.ObservacionClienteWebMapper;
import com.eldiamante360.cliente.presentation.mapper.PrecioClienteProductoWebMapper;
import com.eldiamante360.shared.presentation.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes/{clienteId}")
public class ClienteDetalleController {

    private final RegistrarObservacionClienteUseCase registrarObservacionClienteUseCase;
    private final ListarObservacionesClienteUseCase listarObservacionesClienteUseCase;
    private final ListarHistorialClienteUseCase listarHistorialClienteUseCase;
    private final EstablecerPrecioClienteProductoUseCase establecerPrecioClienteProductoUseCase;
    private final ListarPreciosClienteUseCase listarPreciosClienteUseCase;
    private final EliminarPrecioClienteProductoUseCase eliminarPrecioClienteProductoUseCase;
    private final ObtenerSesionActualUseCase obtenerSesionActualUseCase;
    private final ObservacionClienteWebMapper observacionClienteWebMapper;
    private final HistorialClienteWebMapper historialClienteWebMapper;
    private final PrecioClienteProductoWebMapper precioClienteProductoWebMapper;

    public ClienteDetalleController(RegistrarObservacionClienteUseCase registrarObservacionClienteUseCase,
                                     ListarObservacionesClienteUseCase listarObservacionesClienteUseCase,
                                     ListarHistorialClienteUseCase listarHistorialClienteUseCase,
                                     EstablecerPrecioClienteProductoUseCase establecerPrecioClienteProductoUseCase,
                                     ListarPreciosClienteUseCase listarPreciosClienteUseCase,
                                     EliminarPrecioClienteProductoUseCase eliminarPrecioClienteProductoUseCase,
                                     ObtenerSesionActualUseCase obtenerSesionActualUseCase,
                                     ObservacionClienteWebMapper observacionClienteWebMapper,
                                     HistorialClienteWebMapper historialClienteWebMapper,
                                     PrecioClienteProductoWebMapper precioClienteProductoWebMapper) {
        this.registrarObservacionClienteUseCase = registrarObservacionClienteUseCase;
        this.listarObservacionesClienteUseCase = listarObservacionesClienteUseCase;
        this.listarHistorialClienteUseCase = listarHistorialClienteUseCase;
        this.establecerPrecioClienteProductoUseCase = establecerPrecioClienteProductoUseCase;
        this.listarPreciosClienteUseCase = listarPreciosClienteUseCase;
        this.eliminarPrecioClienteProductoUseCase = eliminarPrecioClienteProductoUseCase;
        this.obtenerSesionActualUseCase = obtenerSesionActualUseCase;
        this.observacionClienteWebMapper = observacionClienteWebMapper;
        this.historialClienteWebMapper = historialClienteWebMapper;
        this.precioClienteProductoWebMapper = precioClienteProductoWebMapper;
    }

    @PostMapping("/observaciones")
    @PreAuthorize("hasAuthority('CLIENTE_CREAR')")
    public ResponseEntity<ObservacionClienteResponse> registrarObservacion(@PathVariable Long clienteId,
                                                                             @Valid @RequestBody RegistrarObservacionRequest request,
                                                                             Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = registrarObservacionClienteUseCase.ejecutar(
                observacionClienteWebMapper.toCommand(clienteId, request, sesion.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(observacionClienteWebMapper.toResponse(resultado));
    }

    @GetMapping("/observaciones")
    @PreAuthorize("hasAuthority('CLIENTE_LEER')")
    public ResponseEntity<PageResponse<ObservacionClienteResponse>> listarObservaciones(@PathVariable Long clienteId,
                                                                                          Pageable pageable) {
        var pagina = listarObservacionesClienteUseCase.ejecutar(clienteId, pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, observacionClienteWebMapper::toResponse));
    }

    @GetMapping("/historial")
    @PreAuthorize("hasAuthority('CLIENTE_LEER')")
    public ResponseEntity<PageResponse<HistorialClienteResponse>> listarHistorial(@PathVariable Long clienteId,
                                                                                    Pageable pageable) {
        var pagina = listarHistorialClienteUseCase.ejecutar(clienteId, pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, historialClienteWebMapper::toResponse));
    }

    @PutMapping("/precios")
    @PreAuthorize("hasAuthority('CLIENTE_EDITAR')")
    public ResponseEntity<PrecioClienteProductoResponse> establecerPrecio(@PathVariable Long clienteId,
                                                                             @Valid @RequestBody EstablecerPrecioClienteProductoRequest request) {
        var resultado = establecerPrecioClienteProductoUseCase.ejecutar(
                precioClienteProductoWebMapper.toCommand(clienteId, request));
        return ResponseEntity.ok(precioClienteProductoWebMapper.toResponse(resultado));
    }

    @GetMapping("/precios")
    @PreAuthorize("hasAuthority('CLIENTE_LEER')")
    public ResponseEntity<PageResponse<PrecioClienteProductoResponse>> listarPrecios(@PathVariable Long clienteId,
                                                                                        Pageable pageable) {
        var pagina = listarPreciosClienteUseCase.ejecutar(clienteId, pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, precioClienteProductoWebMapper::toResponse));
    }

    @DeleteMapping("/precios/{precioId}")
    @PreAuthorize("hasAuthority('CLIENTE_EDITAR')")
    public ResponseEntity<Void> eliminarPrecio(@PathVariable Long clienteId, @PathVariable Long precioId) {
        eliminarPrecioClienteProductoUseCase.ejecutar(clienteId, precioId);
        return ResponseEntity.noContent().build();
    }
}
