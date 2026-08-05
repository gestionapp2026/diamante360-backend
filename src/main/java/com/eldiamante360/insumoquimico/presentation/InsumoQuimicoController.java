package com.eldiamante360.insumoquimico.presentation;

import com.eldiamante360.insumoquimico.application.usecase.ActualizarPrecioCompraInsumoUseCase;
import com.eldiamante360.insumoquimico.application.usecase.CambiarEstadoInsumoQuimicoUseCase;
import com.eldiamante360.insumoquimico.application.usecase.CrearInsumoQuimicoUseCase;
import com.eldiamante360.insumoquimico.application.usecase.EliminarInsumoQuimicoUseCase;
import com.eldiamante360.insumoquimico.application.usecase.ListarInsumosQuimicosUseCase;
import com.eldiamante360.insumoquimico.application.usecase.ListarLotesPorInsumoUseCase;
import com.eldiamante360.insumoquimico.application.usecase.ListarLotesPorVencerUseCase;
import com.eldiamante360.insumoquimico.application.usecase.ObtenerDependenciasInsumoQuimicoUseCase;
import com.eldiamante360.insumoquimico.application.usecase.ObtenerInsumoQuimicoUseCase;
import com.eldiamante360.insumoquimico.presentation.dto.request.ActualizarPrecioCompraRequest;
import com.eldiamante360.insumoquimico.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.request.CrearInsumoQuimicoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.response.InsumoQuimicoResponse;
import com.eldiamante360.insumoquimico.presentation.dto.response.LoteResponse;
import com.eldiamante360.insumoquimico.presentation.mapper.InsumoQuimicoWebMapper;
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
@RequestMapping("/insumos-quimicos")
public class InsumoQuimicoController {

    private final CrearInsumoQuimicoUseCase crearInsumoQuimicoUseCase;
    private final ListarInsumosQuimicosUseCase listarInsumosQuimicosUseCase;
    private final ObtenerInsumoQuimicoUseCase obtenerInsumoQuimicoUseCase;
    private final CambiarEstadoInsumoQuimicoUseCase cambiarEstadoInsumoQuimicoUseCase;
    private final ListarLotesPorInsumoUseCase listarLotesPorInsumoUseCase;
    private final ListarLotesPorVencerUseCase listarLotesPorVencerUseCase;
    private final ActualizarPrecioCompraInsumoUseCase actualizarPrecioCompraInsumoUseCase;
    private final EliminarInsumoQuimicoUseCase eliminarInsumoQuimicoUseCase;
    private final ObtenerDependenciasInsumoQuimicoUseCase obtenerDependenciasInsumoQuimicoUseCase;
    private final InsumoQuimicoWebMapper insumoQuimicoWebMapper;

    public InsumoQuimicoController(CrearInsumoQuimicoUseCase crearInsumoQuimicoUseCase,
                                    ListarInsumosQuimicosUseCase listarInsumosQuimicosUseCase,
                                    ObtenerInsumoQuimicoUseCase obtenerInsumoQuimicoUseCase,
                                    CambiarEstadoInsumoQuimicoUseCase cambiarEstadoInsumoQuimicoUseCase,
                                    ListarLotesPorInsumoUseCase listarLotesPorInsumoUseCase,
                                    ListarLotesPorVencerUseCase listarLotesPorVencerUseCase,
                                    ActualizarPrecioCompraInsumoUseCase actualizarPrecioCompraInsumoUseCase,
                                    EliminarInsumoQuimicoUseCase eliminarInsumoQuimicoUseCase,
                                    ObtenerDependenciasInsumoQuimicoUseCase obtenerDependenciasInsumoQuimicoUseCase,
                                    InsumoQuimicoWebMapper insumoQuimicoWebMapper) {
        this.crearInsumoQuimicoUseCase = crearInsumoQuimicoUseCase;
        this.listarInsumosQuimicosUseCase = listarInsumosQuimicosUseCase;
        this.obtenerInsumoQuimicoUseCase = obtenerInsumoQuimicoUseCase;
        this.cambiarEstadoInsumoQuimicoUseCase = cambiarEstadoInsumoQuimicoUseCase;
        this.listarLotesPorInsumoUseCase = listarLotesPorInsumoUseCase;
        this.listarLotesPorVencerUseCase = listarLotesPorVencerUseCase;
        this.actualizarPrecioCompraInsumoUseCase = actualizarPrecioCompraInsumoUseCase;
        this.eliminarInsumoQuimicoUseCase = eliminarInsumoQuimicoUseCase;
        this.obtenerDependenciasInsumoQuimicoUseCase = obtenerDependenciasInsumoQuimicoUseCase;
        this.insumoQuimicoWebMapper = insumoQuimicoWebMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INSUMO_CREAR')")
    public ResponseEntity<InsumoQuimicoResponse> crear(@Valid @RequestBody CrearInsumoQuimicoRequest request) {
        var resultado = crearInsumoQuimicoUseCase.ejecutar(insumoQuimicoWebMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(insumoQuimicoWebMapper.toResponse(resultado));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INSUMO_LEER')")
    public ResponseEntity<PageResponse<InsumoQuimicoResponse>> listar(Pageable pageable) {
        var pagina = listarInsumosQuimicosUseCase.ejecutar(pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, insumoQuimicoWebMapper::toResponse));
    }

    @GetMapping("/vencimientos")
    @PreAuthorize("hasAuthority('INSUMO_LEER')")
    public ResponseEntity<List<LoteResponse>> listarPorVencer(@RequestParam(defaultValue = "30") int dias) {
        var resultado = listarLotesPorVencerUseCase.ejecutar(dias).stream()
                .map(insumoQuimicoWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INSUMO_LEER')")
    public ResponseEntity<InsumoQuimicoResponse> obtener(@PathVariable Long id) {
        var resultado = obtenerInsumoQuimicoUseCase.ejecutar(id);
        return ResponseEntity.ok(insumoQuimicoWebMapper.toResponse(resultado));
    }

    @GetMapping("/{id}/lotes")
    @PreAuthorize("hasAuthority('INSUMO_LEER')")
    public ResponseEntity<PageResponse<LoteResponse>> listarLotes(@PathVariable Long id, Pageable pageable) {
        var pagina = listarLotesPorInsumoUseCase.ejecutar(id, pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, insumoQuimicoWebMapper::toResponse));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('INSUMO_ELIMINAR')")
    public ResponseEntity<InsumoQuimicoResponse> cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoRequest request) {
        var resultado = cambiarEstadoInsumoQuimicoUseCase.ejecutar(id, request.activo());
        return ResponseEntity.ok(insumoQuimicoWebMapper.toResponse(resultado));
    }

    @PatchMapping("/{id}/precio-compra")
    @PreAuthorize("hasAuthority('INSUMO_CREAR')")
    public ResponseEntity<InsumoQuimicoResponse> actualizarPrecioCompra(@PathVariable Long id,
                                                                          @Valid @RequestBody ActualizarPrecioCompraRequest request) {
        var resultado = actualizarPrecioCompraInsumoUseCase.ejecutar(id, request.precioCompra());
        return ResponseEntity.ok(insumoQuimicoWebMapper.toResponse(resultado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INSUMO_ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                          @RequestParam(name = "cascada", defaultValue = "false") boolean cascada) {
        eliminarInsumoQuimicoUseCase.ejecutar(id, cascada);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dependencias")
    @PreAuthorize("hasAuthority('INSUMO_ELIMINAR')")
    public ResponseEntity<DependenciasResponse> dependencias(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerDependenciasInsumoQuimicoUseCase.ejecutar(id));
    }
}
