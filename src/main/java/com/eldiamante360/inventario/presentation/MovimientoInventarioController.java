package com.eldiamante360.inventario.presentation;

import com.eldiamante360.auth.application.usecase.ObtenerSesionActualUseCase;
import com.eldiamante360.inventario.application.usecase.ListarMovimientosPorProductoUseCase;
import com.eldiamante360.inventario.application.usecase.RegistrarMovimientoInventarioUseCase;
import com.eldiamante360.inventario.presentation.dto.request.RegistrarMovimientoRequest;
import com.eldiamante360.inventario.presentation.dto.response.MovimientoResponse;
import com.eldiamante360.inventario.presentation.mapper.MovimientoWebMapper;
import com.eldiamante360.shared.presentation.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/productos/{productoId}/movimientos")
public class MovimientoInventarioController {

    private final RegistrarMovimientoInventarioUseCase registrarMovimientoInventarioUseCase;
    private final ListarMovimientosPorProductoUseCase listarMovimientosPorProductoUseCase;
    private final ObtenerSesionActualUseCase obtenerSesionActualUseCase;
    private final MovimientoWebMapper movimientoWebMapper;

    public MovimientoInventarioController(RegistrarMovimientoInventarioUseCase registrarMovimientoInventarioUseCase,
                                           ListarMovimientosPorProductoUseCase listarMovimientosPorProductoUseCase,
                                           ObtenerSesionActualUseCase obtenerSesionActualUseCase,
                                           MovimientoWebMapper movimientoWebMapper) {
        this.registrarMovimientoInventarioUseCase = registrarMovimientoInventarioUseCase;
        this.listarMovimientosPorProductoUseCase = listarMovimientosPorProductoUseCase;
        this.obtenerSesionActualUseCase = obtenerSesionActualUseCase;
        this.movimientoWebMapper = movimientoWebMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTARIO_AJUSTAR')")
    public ResponseEntity<MovimientoResponse> registrar(@PathVariable Long productoId,
                                                          @Valid @RequestBody RegistrarMovimientoRequest request,
                                                          Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = registrarMovimientoInventarioUseCase.ejecutar(
                movimientoWebMapper.toCommand(productoId, request, sesion.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoWebMapper.toResponse(resultado));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTARIO_LEER')")
    public ResponseEntity<PageResponse<MovimientoResponse>> listar(@PathVariable Long productoId, Pageable pageable) {
        var pagina = listarMovimientosPorProductoUseCase.ejecutar(productoId, pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, movimientoWebMapper::toResponse));
    }
}
