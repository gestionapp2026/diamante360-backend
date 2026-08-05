-- ============================================================================
-- V4: Modulo de clientes (catalogo, rutas de reparto, observaciones e
-- historial de eventos)
-- ============================================================================

CREATE TABLE ruta (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(80)  NOT NULL,
    descripcion VARCHAR(200),
    activo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_ruta_nombre UNIQUE (nombre)
);

CREATE TABLE cliente (
    id                BIGSERIAL PRIMARY KEY,
    tipo_documento    VARCHAR(10)   NOT NULL,
    numero_documento  VARCHAR(20)   NOT NULL,
    nombre            VARCHAR(150)  NOT NULL,
    telefono          VARCHAR(20),
    email             VARCHAR(120),
    direccion         VARCHAR(200),
    ruta_id           BIGINT        REFERENCES ruta (id),
    activo            BOOLEAN       NOT NULL DEFAULT TRUE,
    version           INTEGER       NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by        VARCHAR(50),
    updated_by        VARCHAR(50),
    CONSTRAINT uq_cliente_numero_documento UNIQUE (numero_documento),
    CONSTRAINT ck_cliente_tipo_documento CHECK (tipo_documento IN ('CC', 'NIT', 'CE', 'PASAPORTE'))
);

CREATE INDEX idx_cliente_ruta ON cliente (ruta_id);
CREATE INDEX idx_cliente_activo ON cliente (activo);
CREATE INDEX idx_cliente_nombre ON cliente (nombre);

CREATE TABLE observacion_cliente (
    id          BIGSERIAL PRIMARY KEY,
    cliente_id  BIGINT        NOT NULL REFERENCES cliente (id),
    texto       VARCHAR(500)  NOT NULL,
    usuario_id  BIGINT        NOT NULL REFERENCES usuario (id),
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_observacion_cliente_cliente ON observacion_cliente (cliente_id, created_at DESC);

CREATE TABLE historial_cliente (
    id          BIGSERIAL PRIMARY KEY,
    cliente_id  BIGINT        NOT NULL REFERENCES cliente (id),
    tipo_evento VARCHAR(30)   NOT NULL,
    descripcion VARCHAR(300)  NOT NULL,
    usuario_id  BIGINT        NOT NULL REFERENCES usuario (id),
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT ck_historial_cliente_tipo_evento
        CHECK (tipo_evento IN ('CREACION', 'EDICION', 'CAMBIO_RUTA', 'ACTIVACION', 'DESACTIVACION'))
);

CREATE INDEX idx_historial_cliente_cliente ON historial_cliente (cliente_id, created_at DESC);

-- ----------------------------------------------------------------------------
-- Nota: los permisos CLIENTE_LEER, CLIENTE_CREAR, CLIENTE_EDITAR y
-- CLIENTE_ELIMINAR ya fueron sembrados en V1__create_seguridad.sql en
-- anticipacion a este modulo, y cubren tambien la gestion de rutas y el
-- registro de observaciones (no se crean permisos nuevos).
-- ----------------------------------------------------------------------------
