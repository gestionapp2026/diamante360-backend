package com.eldiamante360.cliente.presentation;

import com.eldiamante360.auth.application.usecase.ObtenerSesionActualUseCase;
import com.eldiamante360.cliente.application.usecase.ActualizarClienteUseCase;
import com.eldiamante360.cliente.application.usecase.AsignarRutaClienteUseCase;
import com.eldiamante360.cliente.application.usecase.BuscarClientesUseCase;
import com.eldiamante360.cliente.application.usecase.CambiarEstadoClienteUseCase;
import com.eldiamante360.cliente.application.usecase.CrearClienteUseCase;
import com.eldiamante360.cliente.application.usecase.EliminarClienteUseCase;
import com.eldiamante360.cliente.application.usecase.ListarClientesUseCase;
import com.eldiamante360.cliente.application.usecase.ObtenerClienteUseCase;
import com.eldiamante360.cliente.application.usecase.ObtenerDependenciasClienteUseCase;
import com.eldiamante360.cliente.presentation.dto.request.ActualizarClienteRequest;
import com.eldiamante360.cliente.presentation.dto.request.AsignarRutaRequest;
import com.eldiamante360.cliente.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.cliente.presentation.dto.request.CrearClienteRequest;
import com.eldiamante360.cliente.presentation.dto.response.ClienteResponse;
import com.eldiamante360.cliente.presentation.mapper.ClienteWebMapper;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import com.eldiamante360.shared.presentation.PageResponse;
import com.eldiamante360.shared.presentation.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final CrearClienteUseCase crearClienteUseCase;
    private final ActualizarClienteUseCase actualizarClienteUseCase;
    private final ObtenerClienteUseCase obtenerClienteUseCase;
    private final ListarClientesUseCase listarClientesUseCase;
    private final BuscarClientesUseCase buscarClientesUseCase;
    private final CambiarEstadoClienteUseCase cambiarEstadoClienteUseCase;
    private final AsignarRutaClienteUseCase asignarRutaClienteUseCase;
    private final EliminarClienteUseCase eliminarClienteUseCase;
    private final ObtenerDependenciasClienteUseCase obtenerDependenciasClienteUseCase;
    private final ObtenerSesionActualUseCase obtenerSesionActualUseCase;
    private final ClienteWebMapper clienteWebMapper;

    public ClienteController(CrearClienteUseCase crearClienteUseCase,
                              ActualizarClienteUseCase actualizarClienteUseCase,
                              ObtenerClienteUseCase obtenerClienteUseCase,
                              ListarClientesUseCase listarClientesUseCase,
                              BuscarClientesUseCase buscarClientesUseCase,
                              CambiarEstadoClienteUseCase cambiarEstadoClienteUseCase,
                              AsignarRutaClienteUseCase asignarRutaClienteUseCase,
                              EliminarClienteUseCase eliminarClienteUseCase,
                              ObtenerDependenciasClienteUseCase obtenerDependenciasClienteUseCase,
                              ObtenerSesionActualUseCase obtenerSesionActualUseCase,
                              ClienteWebMapper clienteWebMapper) {
        this.crearClienteUseCase = crearClienteUseCase;
        this.actualizarClienteUseCase = actualizarClienteUseCase;
        this.obtenerClienteUseCase = obtenerClienteUseCase;
        this.listarClientesUseCase = listarClientesUseCase;
        this.buscarClientesUseCase = buscarClientesUseCase;
        this.cambiarEstadoClienteUseCase = cambiarEstadoClienteUseCase;
        this.asignarRutaClienteUseCase = asignarRutaClienteUseCase;
        this.eliminarClienteUseCase = eliminarClienteUseCase;
        this.obtenerDependenciasClienteUseCase = obtenerDependenciasClienteUseCase;
        this.obtenerSesionActualUseCase = obtenerSesionActualUseCase;
        this.clienteWebMapper = clienteWebMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENTE_CREAR')")
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody CrearClienteRequest request,
                                                  Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = crearClienteUseCase.ejecutar(clienteWebMapper.toCommand(request, sesion.id()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aplicarMascaraSensible(clienteWebMapper.toResponse(resultado), authentication));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENTE_LEER')")
    public ResponseEntity<PageResponse<ClienteResponse>> listar(@RequestParam(required = false) @Nullable String texto,
                                                                  Pageable pageable,
                                                                  Authentication authentication) {
        var pagina = (texto != null && !texto.isBlank())
                ? buscarClientesUseCase.ejecutar(texto, pageable)
                : listarClientesUseCase.ejecutar(pageable);
        return ResponseEntity.ok(PageResponse.from(pagina,
                resultado -> aplicarMascaraSensible(clienteWebMapper.toResponse(resultado), authentication)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_LEER')")
    public ResponseEntity<ClienteResponse> obtener(@PathVariable Long id, Authentication authentication) {
        var resultado = obtenerClienteUseCase.ejecutar(id);
        return ResponseEntity.ok(aplicarMascaraSensible(clienteWebMapper.toResponse(resultado), authentication));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_EDITAR')")
    public ResponseEntity<ClienteResponse> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody ActualizarClienteRequest request,
                                                        Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = actualizarClienteUseCase.ejecutar(clienteWebMapper.toCommand(id, request, sesion.id()));
        return ResponseEntity.ok(aplicarMascaraSensible(clienteWebMapper.toResponse(resultado), authentication));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('CLIENTE_ELIMINAR')")
    public ResponseEntity<ClienteResponse> cambiarEstado(@PathVariable Long id,
                                                           @Valid @RequestBody CambiarEstadoRequest request,
                                                           Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = cambiarEstadoClienteUseCase.ejecutar(id, request.activo(), sesion.id());
        return ResponseEntity.ok(aplicarMascaraSensible(clienteWebMapper.toResponse(resultado), authentication));
    }

    @PatchMapping("/{id}/ruta")
    @PreAuthorize("hasAuthority('CLIENTE_EDITAR')")
    public ResponseEntity<ClienteResponse> asignarRuta(@PathVariable Long id,
                                                         @Valid @RequestBody AsignarRutaRequest request,
                                                         Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = asignarRutaClienteUseCase.ejecutar(clienteWebMapper.toAsignarRutaCommand(id, request, sesion.id()));
        return ResponseEntity.ok(aplicarMascaraSensible(clienteWebMapper.toResponse(resultado), authentication));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                          @RequestParam(name = "cascada", defaultValue = "false") boolean cascada) {
        eliminarClienteUseCase.ejecutar(id, cascada);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dependencias")
    @PreAuthorize("hasAuthority('CLIENTE_ELIMINAR')")
    public ResponseEntity<DependenciasResponse> dependencias(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerDependenciasClienteUseCase.ejecutar(id));
    }

    /**
     * Enmascara el numero de documento (cedula/RUC) del cliente en la
     * respuesta si el usuario autenticado no tiene el permiso
     * {@code CLIENTE_VER_DOCUMENTO}. No afecta la autorizacion de acceso al
     * endpoint (eso lo controla {@code @PreAuthorize}), solo el contenido
     * del campo sensible en el JSON devuelto.
     */
    private ClienteResponse aplicarMascaraSensible(ClienteResponse response, Authentication authentication) {
        if (SecurityUtils.tieneAutoridad(authentication, "CLIENTE_VER_DOCUMENTO")) {
            return response;
        }
        return clienteWebMapper.enmascararDocumento(response);
    }
}
