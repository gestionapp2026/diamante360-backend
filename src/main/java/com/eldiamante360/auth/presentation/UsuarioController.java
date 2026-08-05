package com.eldiamante360.auth.presentation;

import com.eldiamante360.auth.application.dto.CambiarPasswordCommand;
import com.eldiamante360.auth.application.usecase.*;
import com.eldiamante360.auth.presentation.dto.request.ActualizarUsuarioRequest;
import com.eldiamante360.auth.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.auth.presentation.dto.request.CambiarPasswordRequest;
import com.eldiamante360.auth.presentation.dto.request.CrearUsuarioRequest;
import com.eldiamante360.auth.presentation.dto.response.RestablecerPasswordResponse;
import com.eldiamante360.auth.presentation.dto.response.UsuarioResponse;
import com.eldiamante360.auth.presentation.mapper.UsuarioWebMapper;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import com.eldiamante360.shared.presentation.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final ListarUsuariosUseCase listarUsuariosUseCase;
    private final ObtenerUsuarioUseCase obtenerUsuarioUseCase;
    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase;
    private final CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;
    private final CambiarPasswordUseCase cambiarPasswordUseCase;
    private final RestablecerPasswordUseCase restablecerPasswordUseCase;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;
    private final ObtenerDependenciasUsuarioUseCase obtenerDependenciasUsuarioUseCase;
    private final ObtenerSesionActualUseCase obtenerSesionActualUseCase;
    private final UsuarioWebMapper usuarioWebMapper;

    public UsuarioController(CrearUsuarioUseCase crearUsuarioUseCase,
                              ListarUsuariosUseCase listarUsuariosUseCase,
                              ObtenerUsuarioUseCase obtenerUsuarioUseCase,
                              ActualizarUsuarioUseCase actualizarUsuarioUseCase,
                              CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase,
                              CambiarPasswordUseCase cambiarPasswordUseCase,
                              RestablecerPasswordUseCase restablecerPasswordUseCase,
                              EliminarUsuarioUseCase eliminarUsuarioUseCase,
                              ObtenerDependenciasUsuarioUseCase obtenerDependenciasUsuarioUseCase,
                              ObtenerSesionActualUseCase obtenerSesionActualUseCase,
                              UsuarioWebMapper usuarioWebMapper) {
        this.crearUsuarioUseCase = crearUsuarioUseCase;
        this.listarUsuariosUseCase = listarUsuariosUseCase;
        this.obtenerUsuarioUseCase = obtenerUsuarioUseCase;
        this.actualizarUsuarioUseCase = actualizarUsuarioUseCase;
        this.cambiarEstadoUsuarioUseCase = cambiarEstadoUsuarioUseCase;
        this.cambiarPasswordUseCase = cambiarPasswordUseCase;
        this.restablecerPasswordUseCase = restablecerPasswordUseCase;
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
        this.obtenerDependenciasUsuarioUseCase = obtenerDependenciasUsuarioUseCase;
        this.obtenerSesionActualUseCase = obtenerSesionActualUseCase;
        this.usuarioWebMapper = usuarioWebMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USUARIO_CREAR')")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody CrearUsuarioRequest request) {
        var resultado = crearUsuarioUseCase.ejecutar(usuarioWebMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioWebMapper.toResponse(resultado));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USUARIO_LEER')")
    public ResponseEntity<PageResponse<UsuarioResponse>> listar(Pageable pageable) {
        var pagina = listarUsuariosUseCase.ejecutar(pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, usuarioWebMapper::toResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_LEER')")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
        var resultado = obtenerUsuarioUseCase.ejecutar(id);
        return ResponseEntity.ok(usuarioWebMapper.toResponse(resultado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_EDITAR')")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarUsuarioRequest request) {
        var resultado = actualizarUsuarioUseCase.ejecutar(usuarioWebMapper.toCommand(id, request));
        return ResponseEntity.ok(usuarioWebMapper.toResponse(resultado));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('USUARIO_ELIMINAR')")
    public ResponseEntity<UsuarioResponse> cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoRequest request,
                                                          Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = cambiarEstadoUsuarioUseCase.ejecutar(id, request.activo(), sesion.id());
        return ResponseEntity.ok(usuarioWebMapper.toResponse(resultado));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> cambiarMiPassword(@Valid @RequestBody CambiarPasswordRequest request, Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        cambiarPasswordUseCase.ejecutar(new CambiarPasswordCommand(sesion.id(), request.passwordActual(), request.passwordNueva()));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAuthority('USUARIO_EDITAR')")
    public ResponseEntity<RestablecerPasswordResponse> restablecerPassword(@PathVariable Long id, Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = restablecerPasswordUseCase.ejecutar(id, sesion.id());
        return ResponseEntity.ok(usuarioWebMapper.toResponse(resultado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                          @RequestParam(name = "cascada", defaultValue = "false") boolean cascada,
                                          Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        eliminarUsuarioUseCase.ejecutar(id, sesion.id(), cascada);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dependencias")
    @PreAuthorize("hasAuthority('USUARIO_ELIMINAR')")
    public ResponseEntity<DependenciasResponse> dependencias(@PathVariable Long id, Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        return ResponseEntity.ok(obtenerDependenciasUsuarioUseCase.ejecutar(id, sesion.id()));
    }
}
