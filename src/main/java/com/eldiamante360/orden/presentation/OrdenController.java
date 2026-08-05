package com.eldiamante360.orden.presentation;

import com.eldiamante360.auth.application.usecase.ObtenerSesionActualUseCase;
import com.eldiamante360.orden.application.usecase.AnularOrdenUseCase;
import com.eldiamante360.orden.application.usecase.CrearOrdenUseCase;
import com.eldiamante360.orden.application.usecase.DespacharOrdenUseCase;
import com.eldiamante360.orden.application.usecase.EliminarOrdenUseCase;
import com.eldiamante360.orden.application.usecase.ListarOrdenesUseCase;
import com.eldiamante360.orden.application.usecase.ObtenerDependenciasOrdenUseCase;
import com.eldiamante360.orden.application.usecase.ObtenerOrdenUseCase;
import com.eldiamante360.orden.domain.model.EstadoOrden;
import com.eldiamante360.orden.presentation.dto.request.CrearOrdenRequest;
import com.eldiamante360.orden.presentation.dto.response.OrdenResponse;
import com.eldiamante360.orden.presentation.mapper.OrdenWebMapper;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import com.eldiamante360.shared.presentation.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {

    private final CrearOrdenUseCase crearOrdenUseCase;
    private final ListarOrdenesUseCase listarOrdenesUseCase;
    private final ObtenerOrdenUseCase obtenerOrdenUseCase;
    private final DespacharOrdenUseCase despacharOrdenUseCase;
    private final AnularOrdenUseCase anularOrdenUseCase;
    private final EliminarOrdenUseCase eliminarOrdenUseCase;
    private final ObtenerDependenciasOrdenUseCase obtenerDependenciasOrdenUseCase;
    private final ObtenerSesionActualUseCase obtenerSesionActualUseCase;
    private final OrdenWebMapper ordenWebMapper;

    public OrdenController(CrearOrdenUseCase crearOrdenUseCase,
                            ListarOrdenesUseCase listarOrdenesUseCase,
                            ObtenerOrdenUseCase obtenerOrdenUseCase,
                            DespacharOrdenUseCase despacharOrdenUseCase,
                            AnularOrdenUseCase anularOrdenUseCase,
                            EliminarOrdenUseCase eliminarOrdenUseCase,
                            ObtenerDependenciasOrdenUseCase obtenerDependenciasOrdenUseCase,
                            ObtenerSesionActualUseCase obtenerSesionActualUseCase,
                            OrdenWebMapper ordenWebMapper) {
        this.crearOrdenUseCase = crearOrdenUseCase;
        this.listarOrdenesUseCase = listarOrdenesUseCase;
        this.obtenerOrdenUseCase = obtenerOrdenUseCase;
        this.despacharOrdenUseCase = despacharOrdenUseCase;
        this.anularOrdenUseCase = anularOrdenUseCase;
        this.eliminarOrdenUseCase = eliminarOrdenUseCase;
        this.obtenerDependenciasOrdenUseCase = obtenerDependenciasOrdenUseCase;
        this.obtenerSesionActualUseCase = obtenerSesionActualUseCase;
        this.ordenWebMapper = ordenWebMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ORDEN_CREAR')")
    public ResponseEntity<OrdenResponse> crear(@Valid @RequestBody CrearOrdenRequest request,
                                                Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = crearOrdenUseCase.ejecutar(ordenWebMapper.toCommand(request, sesion.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenWebMapper.toResponse(resultado));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ORDEN_LEER')")
    public ResponseEntity<PageResponse<OrdenResponse>> listar(@RequestParam(required = false) @Nullable Long clienteId,
                                                                @RequestParam(required = false) @Nullable EstadoOrden estado,
                                                                @RequestParam(required = false) @Nullable LocalDate fechaEntregaDesde,
                                                                @RequestParam(required = false) @Nullable LocalDate fechaEntregaHasta,
                                                                @RequestParam(required = false) @Nullable LocalDate fechaCreacionDesde,
                                                                @RequestParam(required = false) @Nullable LocalDate fechaCreacionHasta,
                                                                Pageable pageable) {
        var filtro = ordenWebMapper.toFiltro(clienteId, estado, fechaEntregaDesde, fechaEntregaHasta,
                fechaCreacionDesde, fechaCreacionHasta);
        var pagina = listarOrdenesUseCase.ejecutar(filtro, pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, ordenWebMapper::toResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDEN_LEER')")
    public ResponseEntity<OrdenResponse> obtener(@PathVariable Long id) {
        var resultado = obtenerOrdenUseCase.ejecutar(id);
        return ResponseEntity.ok(ordenWebMapper.toResponse(resultado));
    }

    @PatchMapping("/{id}/despachar")
    @PreAuthorize("hasAuthority('ORDEN_DESPACHAR')")
    public ResponseEntity<OrdenResponse> despachar(@PathVariable Long id, Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = despacharOrdenUseCase.ejecutar(id, sesion.id());
        return ResponseEntity.ok(ordenWebMapper.toResponse(resultado));
    }

    @PatchMapping("/{id}/anular")
    @PreAuthorize("hasAuthority('ORDEN_ANULAR')")
    public ResponseEntity<OrdenResponse> anular(@PathVariable Long id, Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = anularOrdenUseCase.ejecutar(id, sesion.id());
        return ResponseEntity.ok(ordenWebMapper.toResponse(resultado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDEN_ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                          @RequestParam(name = "cascada", defaultValue = "false") boolean cascada) {
        eliminarOrdenUseCase.ejecutar(id, cascada);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dependencias")
    @PreAuthorize("hasAuthority('ORDEN_ELIMINAR')")
    public ResponseEntity<DependenciasResponse> dependencias(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerDependenciasOrdenUseCase.ejecutar(id));
    }
}
