package com.eldiamante360.cliente.presentation;

import com.eldiamante360.cliente.application.usecase.ActualizarRutaUseCase;
import com.eldiamante360.cliente.application.usecase.CambiarEstadoRutaUseCase;
import com.eldiamante360.cliente.application.usecase.CrearRutaUseCase;
import com.eldiamante360.cliente.application.usecase.EliminarRutaUseCase;
import com.eldiamante360.cliente.application.usecase.ListarClientesPorRutaUseCase;
import com.eldiamante360.cliente.application.usecase.ListarRutasUseCase;
import com.eldiamante360.cliente.application.usecase.ObtenerDependenciasRutaUseCase;
import com.eldiamante360.cliente.application.usecase.ObtenerRutaUseCase;
import com.eldiamante360.cliente.presentation.dto.request.ActualizarRutaRequest;
import com.eldiamante360.cliente.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.cliente.presentation.dto.request.CrearRutaRequest;
import com.eldiamante360.cliente.presentation.dto.response.ClienteResponse;
import com.eldiamante360.cliente.presentation.dto.response.RutaResponse;
import com.eldiamante360.cliente.presentation.mapper.ClienteWebMapper;
import com.eldiamante360.cliente.presentation.mapper.RutaWebMapper;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import com.eldiamante360.shared.presentation.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rutas")
public class RutaController {

    private final CrearRutaUseCase crearRutaUseCase;
    private final ActualizarRutaUseCase actualizarRutaUseCase;
    private final ListarRutasUseCase listarRutasUseCase;
    private final ObtenerRutaUseCase obtenerRutaUseCase;
    private final CambiarEstadoRutaUseCase cambiarEstadoRutaUseCase;
    private final ListarClientesPorRutaUseCase listarClientesPorRutaUseCase;
    private final EliminarRutaUseCase eliminarRutaUseCase;
    private final ObtenerDependenciasRutaUseCase obtenerDependenciasRutaUseCase;
    private final RutaWebMapper rutaWebMapper;
    private final ClienteWebMapper clienteWebMapper;

    public RutaController(CrearRutaUseCase crearRutaUseCase,
                           ActualizarRutaUseCase actualizarRutaUseCase,
                           ListarRutasUseCase listarRutasUseCase,
                           ObtenerRutaUseCase obtenerRutaUseCase,
                           CambiarEstadoRutaUseCase cambiarEstadoRutaUseCase,
                           ListarClientesPorRutaUseCase listarClientesPorRutaUseCase,
                           EliminarRutaUseCase eliminarRutaUseCase,
                           ObtenerDependenciasRutaUseCase obtenerDependenciasRutaUseCase,
                           RutaWebMapper rutaWebMapper,
                           ClienteWebMapper clienteWebMapper) {
        this.crearRutaUseCase = crearRutaUseCase;
        this.actualizarRutaUseCase = actualizarRutaUseCase;
        this.listarRutasUseCase = listarRutasUseCase;
        this.obtenerRutaUseCase = obtenerRutaUseCase;
        this.cambiarEstadoRutaUseCase = cambiarEstadoRutaUseCase;
        this.listarClientesPorRutaUseCase = listarClientesPorRutaUseCase;
        this.eliminarRutaUseCase = eliminarRutaUseCase;
        this.obtenerDependenciasRutaUseCase = obtenerDependenciasRutaUseCase;
        this.rutaWebMapper = rutaWebMapper;
        this.clienteWebMapper = clienteWebMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENTE_CREAR')")
    public ResponseEntity<RutaResponse> crear(@Valid @RequestBody CrearRutaRequest request) {
        var resultado = crearRutaUseCase.ejecutar(rutaWebMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(rutaWebMapper.toResponse(resultado));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENTE_LEER')")
    public ResponseEntity<List<RutaResponse>> listar() {
        var resultado = listarRutasUseCase.ejecutar().stream()
                .map(rutaWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_LEER')")
    public ResponseEntity<RutaResponse> obtener(@PathVariable Long id) {
        var resultado = obtenerRutaUseCase.ejecutar(id);
        return ResponseEntity.ok(rutaWebMapper.toResponse(resultado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_EDITAR')")
    public ResponseEntity<RutaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarRutaRequest request) {
        var resultado = actualizarRutaUseCase.ejecutar(rutaWebMapper.toCommand(id, request));
        return ResponseEntity.ok(rutaWebMapper.toResponse(resultado));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('CLIENTE_ELIMINAR')")
    public ResponseEntity<RutaResponse> cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoRequest request) {
        var resultado = cambiarEstadoRutaUseCase.ejecutar(id, request.activo());
        return ResponseEntity.ok(rutaWebMapper.toResponse(resultado));
    }

    @GetMapping("/{id}/clientes")
    @PreAuthorize("hasAuthority('CLIENTE_LEER')")
    public ResponseEntity<PageResponse<ClienteResponse>> listarClientes(@PathVariable Long id, Pageable pageable) {
        var pagina = listarClientesPorRutaUseCase.ejecutar(id, pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, clienteWebMapper::toResponse));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                          @RequestParam(name = "cascada", defaultValue = "false") boolean cascada) {
        eliminarRutaUseCase.ejecutar(id, cascada);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dependencias")
    @PreAuthorize("hasAuthority('CLIENTE_ELIMINAR')")
    public ResponseEntity<DependenciasResponse> dependencias(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerDependenciasRutaUseCase.ejecutar(id));
    }
}
