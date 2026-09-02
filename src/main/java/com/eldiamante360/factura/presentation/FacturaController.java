package com.eldiamante360.factura.presentation;

import com.eldiamante360.auth.application.usecase.ObtenerSesionActualUseCase;
import com.eldiamante360.factura.application.usecase.AnularFacturaUseCase;
import com.eldiamante360.factura.application.usecase.CrearFacturaUseCase;
import com.eldiamante360.factura.application.usecase.EliminarFacturaUseCase;
import com.eldiamante360.factura.application.usecase.ListarFacturasPorClienteUseCase;
import com.eldiamante360.factura.application.usecase.ListarFacturasPorEstadoUseCase;
import com.eldiamante360.factura.application.usecase.ListarFacturasUseCase;
import com.eldiamante360.factura.application.usecase.ListarHistorialFacturaUseCase;
import com.eldiamante360.factura.application.usecase.ObtenerDependenciasFacturaUseCase;
import com.eldiamante360.factura.application.usecase.ObtenerFacturaUseCase;
import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.infrastructure.pdf.FacturaPdfService;
import com.eldiamante360.factura.presentation.dto.request.CrearFacturaRequest;
import com.eldiamante360.factura.presentation.dto.response.FacturaResponse;
import com.eldiamante360.factura.presentation.dto.response.HistorialFacturaResponse;
import com.eldiamante360.factura.presentation.mapper.FacturaWebMapper;
import com.eldiamante360.factura.presentation.mapper.HistorialFacturaWebMapper;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import com.eldiamante360.shared.presentation.PageResponse;
import com.eldiamante360.shared.presentation.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    private final CrearFacturaUseCase crearFacturaUseCase;
    private final ObtenerFacturaUseCase obtenerFacturaUseCase;
    private final ListarFacturasUseCase listarFacturasUseCase;
    private final ListarFacturasPorClienteUseCase listarFacturasPorClienteUseCase;
    private final ListarFacturasPorEstadoUseCase listarFacturasPorEstadoUseCase;
    private final AnularFacturaUseCase anularFacturaUseCase;
    private final ListarHistorialFacturaUseCase listarHistorialFacturaUseCase;
    private final EliminarFacturaUseCase eliminarFacturaUseCase;
    private final ObtenerDependenciasFacturaUseCase obtenerDependenciasFacturaUseCase;
    private final ObtenerSesionActualUseCase obtenerSesionActualUseCase;
    private final FacturaWebMapper facturaWebMapper;
    private final HistorialFacturaWebMapper historialFacturaWebMapper;
    private final FacturaPdfService facturaPdfService;

    public FacturaController(CrearFacturaUseCase crearFacturaUseCase,
                              ObtenerFacturaUseCase obtenerFacturaUseCase,
                              ListarFacturasUseCase listarFacturasUseCase,
                              ListarFacturasPorClienteUseCase listarFacturasPorClienteUseCase,
                              ListarFacturasPorEstadoUseCase listarFacturasPorEstadoUseCase,
                              AnularFacturaUseCase anularFacturaUseCase,
                              ListarHistorialFacturaUseCase listarHistorialFacturaUseCase,
                              EliminarFacturaUseCase eliminarFacturaUseCase,
                              ObtenerDependenciasFacturaUseCase obtenerDependenciasFacturaUseCase,
                              ObtenerSesionActualUseCase obtenerSesionActualUseCase,
                              FacturaWebMapper facturaWebMapper,
                              HistorialFacturaWebMapper historialFacturaWebMapper,
                              FacturaPdfService facturaPdfService) {
        this.crearFacturaUseCase = crearFacturaUseCase;
        this.obtenerFacturaUseCase = obtenerFacturaUseCase;
        this.listarFacturasUseCase = listarFacturasUseCase;
        this.listarFacturasPorClienteUseCase = listarFacturasPorClienteUseCase;
        this.listarFacturasPorEstadoUseCase = listarFacturasPorEstadoUseCase;
        this.anularFacturaUseCase = anularFacturaUseCase;
        this.listarHistorialFacturaUseCase = listarHistorialFacturaUseCase;
        this.eliminarFacturaUseCase = eliminarFacturaUseCase;
        this.obtenerDependenciasFacturaUseCase = obtenerDependenciasFacturaUseCase;
        this.obtenerSesionActualUseCase = obtenerSesionActualUseCase;
        this.facturaWebMapper = facturaWebMapper;
        this.historialFacturaWebMapper = historialFacturaWebMapper;
        this.facturaPdfService = facturaPdfService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FACTURA_CREAR')")
    public ResponseEntity<FacturaResponse> crear(@Valid @RequestBody CrearFacturaRequest request,
                                                  Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = crearFacturaUseCase.ejecutar(facturaWebMapper.toCommand(request, sesion.id()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aplicarMascaraSensible(facturaWebMapper.toResponse(resultado), authentication));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('FACTURA_LEER')")
    public ResponseEntity<PageResponse<FacturaResponse>> listar(@RequestParam(required = false) @Nullable Long clienteId,
                                                                  @RequestParam(required = false) @Nullable EstadoFactura estado,
                                                                  Pageable pageable,
                                                                  Authentication authentication) {
        var pagina = clienteId != null
                ? listarFacturasPorClienteUseCase.ejecutar(clienteId, pageable)
                : estado != null
                    ? listarFacturasPorEstadoUseCase.ejecutar(estado, pageable)
                    : listarFacturasUseCase.ejecutar(pageable);
        return ResponseEntity.ok(PageResponse.from(pagina,
                resultado -> aplicarMascaraSensible(facturaWebMapper.toResponse(resultado), authentication)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FACTURA_LEER')")
    public ResponseEntity<FacturaResponse> obtener(@PathVariable Long id, Authentication authentication) {
        var resultado = obtenerFacturaUseCase.ejecutar(id);
        return ResponseEntity.ok(aplicarMascaraSensible(facturaWebMapper.toResponse(resultado), authentication));
    }

    @PatchMapping("/{id}/anular")
    @PreAuthorize("hasAuthority('FACTURA_ANULAR')")
    public ResponseEntity<FacturaResponse> anular(@PathVariable Long id, Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = anularFacturaUseCase.ejecutar(id, sesion.id());
        return ResponseEntity.ok(aplicarMascaraSensible(facturaWebMapper.toResponse(resultado), authentication));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAuthority('FACTURA_LEER')")
    public ResponseEntity<byte[]> obtenerPdf(@PathVariable Long id) {
        var factura = obtenerFacturaUseCase.ejecutar(id);
        byte[] pdf = facturaPdfService.generar(factura);
        String archivo = "factura-" + factura.numero() + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(archivo).build().toString())
                .body(pdf);
    }

    @GetMapping("/{id}/historial")
    @PreAuthorize("hasAuthority('FACTURA_LEER')")
    public ResponseEntity<PageResponse<HistorialFacturaResponse>> listarHistorial(@PathVariable Long id,
                                                                                    Pageable pageable) {
        var pagina = listarHistorialFacturaUseCase.ejecutar(id, pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, historialFacturaWebMapper::toResponse));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FACTURA_ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                          @RequestParam(name = "cascada", defaultValue = "false") boolean cascada) {
        eliminarFacturaUseCase.ejecutar(id, cascada);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dependencias")
    @PreAuthorize("hasAuthority('FACTURA_ELIMINAR')")
    public ResponseEntity<DependenciasResponse> dependencias(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerDependenciasFacturaUseCase.ejecutar(id));
    }

    /**
     * Enmascara el numero de documento (cedula/RUC) del cliente en la
     * respuesta si el usuario autenticado no tiene el permiso
     * {@code CLIENTE_VER_DOCUMENTO}. No afecta la autorizacion de acceso al
     * endpoint (eso lo controla {@code @PreAuthorize}), solo el contenido
     * del campo sensible en el JSON devuelto.
     */
    private FacturaResponse aplicarMascaraSensible(FacturaResponse response, Authentication authentication) {
        if (SecurityUtils.tieneAutoridad(authentication, "CLIENTE_VER_DOCUMENTO")) {
            return response;
        }
        return facturaWebMapper.enmascararDocumento(response);
    }
}
