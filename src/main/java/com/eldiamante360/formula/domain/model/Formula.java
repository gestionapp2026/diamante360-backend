package com.eldiamante360.formula.domain.model;

import com.eldiamante360.formula.domain.exception.CantidadBaseInvalidaException;
import com.eldiamante360.formula.domain.exception.DetalleFormulaVacioException;
import com.eldiamante360.formula.domain.exception.InsumoDuplicadoEnFormulaException;
import com.eldiamante360.formula.domain.exception.NumeroDetalleDuplicadoException;
import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Formula (receta) para producir un producto a partir de quimicos (insumos).
 * Regla de negocio: solo puede existir una formula por producto (se aplica a
 * nivel de aplicacion/BD, no aqui). Los operarios de planta solo deben ver
 * el numero de frasco de cada {@link DetalleFormula}, nunca el nombre del
 * quimico; esa mascara es una preocupacion de presentacion, no de dominio.
 */
public class Formula {

    private final Long id;
    private final Long productoId;
    private final String productoNombre;
    private BigDecimal cantidadBase;
    private UnidadMedidaInsumo unidadBase;
    private boolean activo;
    private final Integer version;
    private List<DetalleFormula> detalles;

    public Formula(Long id, Long productoId, String productoNombre, BigDecimal cantidadBase,
                    UnidadMedidaInsumo unidadBase, boolean activo, Integer version, List<DetalleFormula> detalles) {
        this.id = id;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.cantidadBase = cantidadBase;
        this.unidadBase = unidadBase;
        this.activo = activo;
        this.version = version;
        this.detalles = List.copyOf(detalles);
    }

    public static Formula nueva(Long productoId, String productoNombre, BigDecimal cantidadBase,
                                 UnidadMedidaInsumo unidadBase, List<DetalleFormula> detalles) {
        validarCantidadBase(cantidadBase);
        validarDetalles(detalles);
        return new Formula(null, productoId, productoNombre, cantidadBase, unidadBase, true, null, detalles);
    }

    public void actualizar(BigDecimal cantidadBase, UnidadMedidaInsumo unidadBase, List<DetalleFormula> nuevosDetalles) {
        validarCantidadBase(cantidadBase);
        validarDetalles(nuevosDetalles);
        this.cantidadBase = cantidadBase;
        this.unidadBase = unidadBase;
        this.detalles = List.copyOf(nuevosDetalles);
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    private static void validarCantidadBase(BigDecimal cantidadBase) {
        if (cantidadBase == null || cantidadBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CantidadBaseInvalidaException();
        }
    }

    private static void validarDetalles(List<DetalleFormula> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new DetalleFormulaVacioException();
        }
        Set<Integer> numerosVistos = new HashSet<>();
        Set<Long> insumosVistos = new HashSet<>();
        for (DetalleFormula detalle : detalles) {
            if (!numerosVistos.add(detalle.numero())) {
                throw new NumeroDetalleDuplicadoException(detalle.numero());
            }
            if (!insumosVistos.add(detalle.insumoId())) {
                throw new InsumoDuplicadoEnFormulaException(detalle.insumoId());
            }
        }
    }

    public Long getId() {
        return id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public BigDecimal getCantidadBase() {
        return cantidadBase;
    }

    public UnidadMedidaInsumo getUnidadBase() {
        return unidadBase;
    }

    public boolean isActivo() {
        return activo;
    }

    public Integer getVersion() {
        return version;
    }

    public List<DetalleFormula> getDetalles() {
        return detalles;
    }
}
