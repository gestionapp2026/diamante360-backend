package com.eldiamante360.insumoquimico.presentation;

import com.eldiamante360.auth.application.usecase.ObtenerSesionActualUseCase;
import com.eldiamante360.insumoquimico.application.usecase.ListarMovimientosPorInsumoUseCase;
import com.eldiamante360.insumoquimico.application.usecase.RegistrarEntradaInsumoUseCase;
import com.eldiamante360.insumoquimico.application.usecase.RegistrarSalidaInsumoUseCase;
import com.eldiamante360.insumoquimico.presentation.dto.request.RegistrarEntradaInsumoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.request.RegistrarSalidaInsumoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.response.MovimientoInsumoResponse;
import com.eldiamante360.insumoquimico.presentation.mapper.MovimientoInsumoWebMapper;
import com.eldiamante360.shared.presentation.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/insumos-quimicos/{insumoId}")
public class MovimientoInsumoController {

    private final RegistrarEntradaInsumoUseCase registrarEntradaInsumoUseCase;
    private final RegistrarSalidaInsumoUseCase registrarSalidaInsumoUseCase;
    private final ListarMovimientosPorInsumoUseCase listarMovimientosPorInsumoUseCase;
    private final ObtenerSesionActualUseCase obtenerSesionActualUseCase;
    private final MovimientoInsumoWebMapper movimientoInsumoWebMapper;

    public MovimientoInsumoController(RegistrarEntradaInsumoUseCase registrarEntradaInsumoUseCase,
                                       RegistrarSalidaInsumoUseCase registrarSalidaInsumoUseCase,
                                       ListarMovimientosPorInsumoUseCase listarMovimientosPorInsumoUseCase,
                                       ObtenerSesionActualUseCase obtenerSesionActualUseCase,
                                       MovimientoInsumoWebMapper movimientoInsumoWebMapper) {
        this.registrarEntradaInsumoUseCase = registrarEntradaInsumoUseCase;
        this.registrarSalidaInsumoUseCase = registrarSalidaInsumoUseCase;
        this.listarMovimientosPorInsumoUseCase = listarMovimientosPorInsumoUseCase;
        this.obtenerSesionActualUseCase = obtenerSesionActualUseCase;
        this.movimientoInsumoWebMapper = movimientoInsumoWebMapper;
    }

    @PostMapping("/entradas")
    @PreAuthorize("hasAuthority('INSUMO_AJUSTAR')")
    public ResponseEntity<MovimientoInsumoResponse> registrarEntrada(@PathVariable Long insumoId,
                                                                       @Valid @RequestBody RegistrarEntradaInsumoRequest request,
                                                                       Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = registrarEntradaInsumoUseCase.ejecutar(
                movimientoInsumoWebMapper.toCommand(insumoId, request, sesion.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoInsumoWebMapper.toResponse(resultado));
    }

    @PostMapping("/salidas")
    @PreAuthorize("hasAuthority('INSUMO_AJUSTAR')")
    public ResponseEntity<MovimientoInsumoResponse> registrarSalida(@PathVariable Long insumoId,
                                                                      @Valid @RequestBody RegistrarSalidaInsumoRequest request,
                                                                      Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = registrarSalidaInsumoUseCase.ejecutar(
                movimientoInsumoWebMapper.toCommand(insumoId, request, sesion.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoInsumoWebMapper.toResponse(resultado));
    }

    @GetMapping("/movimientos")
    @PreAuthorize("hasAuthority('INSUMO_LEER')")
    public ResponseEntity<PageResponse<MovimientoInsumoResponse>> listar(@PathVariable Long insumoId, Pageable pageable) {
        var pagina = listarMovimientosPorInsumoUseCase.ejecutar(insumoId, pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, movimientoInsumoWebMapper::toResponse));
    }
}
