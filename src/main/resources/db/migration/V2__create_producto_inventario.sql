-- ============================================================================
-- V2: Modulo de productos (catalogo, categorias, stock) e inventario (kardex)
-- ============================================================================

CREATE TABLE categoria_producto (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL,
    descripcion VARCHAR(150),
    activo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by  VARCHAR(50),
    updated_by  VARCHAR(50),
    CONSTRAINT uq_categoria_producto_nombre UNIQUE (nombre)
);

CREATE TABLE producto (
    id             BIGSERIAL PRIMARY KEY,
    nombre         VARCHAR(120)   NOT NULL,
    categoria_id   BIGINT         NOT NULL REFERENCES categoria_producto (id),
    tipo_venta     VARCHAR(20)    NOT NULL,
    unidad_medida  VARCHAR(10)    NOT NULL,
    precio_compra  NUMERIC(12,2)  NOT NULL,
    precio_venta   NUMERIC(12,2)  NOT NULL,
    stock_actual   NUMERIC(12,3)  NOT NULL DEFAULT 0,
    stock_minimo   NUMERIC(12,3)  NOT NULL DEFAULT 0,
    activo         BOOLEAN        NOT NULL DEFAULT TRUE,
    version        INTEGER        NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by     VARCHAR(50),
    updated_by     VARCHAR(50),
    CONSTRAINT uq_producto_nombre UNIQUE (nombre),
    CONSTRAINT ck_producto_tipo_venta CHECK (tipo_venta IN ('UNIDAD', 'PESO_VARIABLE')),
    CONSTRAINT ck_producto_unidad_medida CHECK (unidad_medida IN ('UND', 'KG', 'LB')),
    CONSTRAINT ck_producto_precio_compra CHECK (precio_compra >= 0),
    CONSTRAINT ck_producto_precio_venta CHECK (precio_venta >= 0),
    CONSTRAINT ck_producto_stock_actual CHECK (stock_actual >= 0),
    CONSTRAINT ck_producto_stock_minimo CHECK (stock_minimo >= 0)
);

CREATE INDEX idx_producto_categoria ON producto (categoria_id);
CREATE INDEX idx_producto_activo ON producto (activo);

CREATE TABLE movimiento_inventario (
    id                 BIGSERIAL PRIMARY KEY,
    producto_id        BIGINT         NOT NULL REFERENCES producto (id),
    tipo_movimiento    VARCHAR(20)    NOT NULL,
    cantidad           NUMERIC(12,3)  NOT NULL,
    stock_resultante   NUMERIC(12,3)  NOT NULL,
    motivo             VARCHAR(200)   NOT NULL,
    usuario_id         BIGINT         NOT NULL REFERENCES usuario (id),
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT ck_movimiento_tipo CHECK (tipo_movimiento IN ('ENTRADA', 'SALIDA', 'AJUSTE')),
    CONSTRAINT ck_movimiento_cantidad CHECK (cantidad >= 0),
    CONSTRAINT ck_movimiento_stock_resultante CHECK (stock_resultante >= 0)
);

CREATE INDEX idx_movimiento_producto ON movimiento_inventario (producto_id, created_at DESC);

-- ----------------------------------------------------------------------------
-- Nuevo permiso: registrar movimientos de inventario (entrada/salida/ajuste)
-- ----------------------------------------------------------------------------
INSERT INTO permiso (codigo, descripcion, modulo) VALUES
    ('INVENTARIO_AJUSTAR', 'Registrar movimientos de inventario (entrada/salida/ajuste)', 'INVENTARIO');

INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r
CROSS JOIN permiso p
WHERE r.nombre = 'ADMIN'
  AND p.codigo = 'INVENTARIO_AJUSTAR';
