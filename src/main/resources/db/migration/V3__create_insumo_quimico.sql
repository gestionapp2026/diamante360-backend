-- ============================================================================
-- V3: Modulo de insumos quimicos (catalogo, lotes con vencimiento) e
-- inventario de insumos (entradas, salidas, kardex)
-- ============================================================================

CREATE TABLE insumo_quimico (
    id             BIGSERIAL PRIMARY KEY,
    nombre         VARCHAR(120)   NOT NULL,
    unidad_medida  VARCHAR(10)    NOT NULL,
    stock_actual   NUMERIC(12,3)  NOT NULL DEFAULT 0,
    activo         BOOLEAN        NOT NULL DEFAULT TRUE,
    version        INTEGER        NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by     VARCHAR(50),
    updated_by     VARCHAR(50),
    CONSTRAINT uq_insumo_quimico_nombre UNIQUE (nombre),
    CONSTRAINT ck_insumo_quimico_unidad_medida CHECK (unidad_medida IN ('UND', 'KG', 'GR', 'LT', 'ML')),
    CONSTRAINT ck_insumo_quimico_stock_actual CHECK (stock_actual >= 0)
);

CREATE INDEX idx_insumo_quimico_activo ON insumo_quimico (activo);

CREATE TABLE lote_insumo (
    id                 BIGSERIAL PRIMARY KEY,
    insumo_id          BIGINT         NOT NULL REFERENCES insumo_quimico (id),
    numero_lote        VARCHAR(60),
    fecha_vencimiento  DATE,
    cantidad_actual    NUMERIC(12,3)  NOT NULL,
    fecha_ingreso      TIMESTAMPTZ    NOT NULL DEFAULT now(),
    version            INTEGER        NOT NULL DEFAULT 0,
    CONSTRAINT ck_lote_insumo_cantidad_actual CHECK (cantidad_actual >= 0)
);

CREATE INDEX idx_lote_insumo_insumo ON lote_insumo (insumo_id);
CREATE INDEX idx_lote_insumo_vencimiento ON lote_insumo (fecha_vencimiento);

CREATE TABLE movimiento_insumo (
    id                 BIGSERIAL PRIMARY KEY,
    insumo_id          BIGINT         NOT NULL REFERENCES insumo_quimico (id),
    lote_id            BIGINT         NOT NULL REFERENCES lote_insumo (id),
    tipo_movimiento    VARCHAR(20)    NOT NULL,
    cantidad           NUMERIC(12,3)  NOT NULL,
    stock_resultante   NUMERIC(12,3)  NOT NULL,
    motivo             VARCHAR(200)   NOT NULL,
    usuario_id         BIGINT         NOT NULL REFERENCES usuario (id),
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT ck_movimiento_insumo_tipo CHECK (tipo_movimiento IN ('ENTRADA', 'SALIDA')),
    CONSTRAINT ck_movimiento_insumo_cantidad CHECK (cantidad >= 0),
    CONSTRAINT ck_movimiento_insumo_stock_resultante CHECK (stock_resultante >= 0)
);

CREATE INDEX idx_movimiento_insumo_insumo ON movimiento_insumo (insumo_id, created_at DESC);

-- ----------------------------------------------------------------------------
-- Nuevo permiso: registrar entradas/salidas de insumos quimicos
-- ----------------------------------------------------------------------------
INSERT INTO permiso (codigo, descripcion, modulo) VALUES
    ('INSUMO_AJUSTAR', 'Registrar entradas y salidas de insumos quimicos', 'INSUMO');

INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r
CROSS JOIN permiso p
WHERE r.nombre = 'ADMIN'
  AND p.codigo = 'INSUMO_AJUSTAR';
