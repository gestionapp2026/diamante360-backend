-- ============================================================================
-- V15: Modulo de formulas (recetas) de produccion. Una formula define cuanto
-- de cada quimico (insumo) se necesita para producir una cantidad base de un
-- producto. Solo puede existir una formula por producto (UNIQUE producto_id).
-- El numero de frasco (detalle_formula.numero) es unico solo DENTRO de cada
-- formula, no globalmente: el mismo numero de frasco puede representar un
-- quimico distinto en otra formula (confirmado con datos reales del negocio).
-- ============================================================================

CREATE TABLE formula (
    id              BIGSERIAL      PRIMARY KEY,
    producto_id     BIGINT         NOT NULL REFERENCES producto (id),
    producto_nombre VARCHAR(120)   NOT NULL,
    cantidad_base   NUMERIC(12,3)  NOT NULL,
    unidad_base     VARCHAR(10)    NOT NULL,
    activo          BOOLEAN        NOT NULL DEFAULT TRUE,
    version         INTEGER        NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by      VARCHAR(50),
    updated_by      VARCHAR(50),
    CONSTRAINT uq_formula_producto UNIQUE (producto_id),
    CONSTRAINT ck_formula_cantidad_base CHECK (cantidad_base > 0),
    CONSTRAINT ck_formula_unidad_base CHECK (unidad_base IN ('UND', 'KG', 'GR', 'LT', 'ML'))
);

CREATE INDEX idx_formula_activo ON formula (activo);

CREATE TABLE detalle_formula (
    id          BIGSERIAL      PRIMARY KEY,
    formula_id  BIGINT         NOT NULL REFERENCES formula (id) ON DELETE CASCADE,
    insumo_id   BIGINT         NOT NULL REFERENCES insumo_quimico (id),
    -- INTEGER (no SMALLINT): debe coincidir con el tipo Integer sin override
    -- de columna en DetalleFormulaEntity.numero (Hibernate ddl-auto=validate
    -- rechaza int2 cuando el mapeo por defecto de Integer espera int4).
    numero      INTEGER        NOT NULL,
    -- precision/scale (14,5) en vez de (12,4): datos reales de negocio traen
    -- hasta 5 decimales (ej. 0.00123 KG de Goma xantana, 1.33339 KG de
    -- Azucar morena), debe coincidir exactamente con DetalleFormulaEntity.
    cantidad    NUMERIC(14,5)  NOT NULL,
    CONSTRAINT uq_detalle_formula_numero UNIQUE (formula_id, numero),
    CONSTRAINT ck_detalle_formula_cantidad CHECK (cantidad > 0)
);

CREATE INDEX idx_detalle_formula_formula ON detalle_formula (formula_id);
CREATE INDEX idx_detalle_formula_insumo ON detalle_formula (insumo_id);
