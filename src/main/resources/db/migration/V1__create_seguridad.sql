-- ============================================================================
-- V1: Modulo de seguridad (auth) - roles, permisos, usuarios, refresh tokens
-- ============================================================================

CREATE TABLE rol (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(30)  NOT NULL,
    descripcion VARCHAR(150),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_rol_nombre UNIQUE (nombre)
);

CREATE TABLE permiso (
    id          BIGSERIAL PRIMARY KEY,
    codigo      VARCHAR(60)  NOT NULL,
    descripcion VARCHAR(150) NOT NULL,
    modulo      VARCHAR(40)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_permiso_codigo UNIQUE (codigo)
);

CREATE INDEX idx_permiso_modulo ON permiso (modulo);

CREATE TABLE rol_permiso (
    rol_id     BIGINT NOT NULL REFERENCES rol (id) ON DELETE CASCADE,
    permiso_id BIGINT NOT NULL REFERENCES permiso (id) ON DELETE CASCADE,
    CONSTRAINT pk_rol_permiso PRIMARY KEY (rol_id, permiso_id)
);

CREATE TABLE usuario (
    id                     BIGSERIAL PRIMARY KEY,
    username               VARCHAR(50)  NOT NULL,
    password_hash          VARCHAR(100) NOT NULL,
    nombre_completo        VARCHAR(120) NOT NULL,
    rol_id                 BIGINT       NOT NULL REFERENCES rol (id),
    activo                 BOOLEAN      NOT NULL DEFAULT TRUE,
    debe_cambiar_password  BOOLEAN      NOT NULL DEFAULT TRUE,
    ultimo_login           TIMESTAMPTZ,
    version                INTEGER      NOT NULL DEFAULT 0,
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by             VARCHAR(50),
    updated_by             VARCHAR(50),
    CONSTRAINT uq_usuario_username UNIQUE (username)
);

CREATE INDEX idx_usuario_rol ON usuario (rol_id);

CREATE TABLE refresh_token (
    id                BIGSERIAL PRIMARY KEY,
    usuario_id        BIGINT       NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    token_hash        VARCHAR(128) NOT NULL,
    fecha_expiracion  TIMESTAMPTZ  NOT NULL,
    revocado          BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_revocacion  TIMESTAMPTZ,
    ip_origen         VARCHAR(45),
    user_agent        VARCHAR(255),
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_refresh_token_hash UNIQUE (token_hash)
);

CREATE INDEX idx_refresh_token_usuario ON refresh_token (usuario_id);
CREATE INDEX idx_refresh_token_expiracion ON refresh_token (fecha_expiracion);

-- ----------------------------------------------------------------------------
-- Datos semilla: roles
-- ----------------------------------------------------------------------------
INSERT INTO rol (nombre, descripcion) VALUES
    ('ADMIN', 'Acceso total al sistema'),
    ('VENDEDOR', 'Genera facturas y consulta cartera de deudores');

-- ----------------------------------------------------------------------------
-- Datos semilla: permisos (codigo, descripcion, modulo)
-- ----------------------------------------------------------------------------
INSERT INTO permiso (codigo, descripcion, modulo) VALUES
    ('DASHBOARD_LEER',        'Ver panel principal',                 'DASHBOARD'),
    ('PRODUCTO_LEER',         'Consultar productos',                 'PRODUCTO'),
    ('PRODUCTO_CREAR',        'Crear productos',                     'PRODUCTO'),
    ('PRODUCTO_EDITAR',       'Editar productos',                    'PRODUCTO'),
    ('PRODUCTO_ELIMINAR',     'Eliminar productos',                  'PRODUCTO'),
    ('INSUMO_LEER',           'Consultar insumos/quimicos',          'INSUMO'),
    ('INSUMO_CREAR',          'Crear insumos/quimicos',              'INSUMO'),
    ('INSUMO_ELIMINAR',       'Eliminar insumos/quimicos',           'INSUMO'),
    ('CLIENTE_LEER',          'Consultar clientes',                  'CLIENTE'),
    ('CLIENTE_CREAR',         'Crear clientes',                      'CLIENTE'),
    ('CLIENTE_EDITAR',        'Editar clientes',                     'CLIENTE'),
    ('CLIENTE_ELIMINAR',      'Eliminar clientes',                   'CLIENTE'),
    ('FACTURA_LEER',          'Consultar facturas',                  'FACTURA'),
    ('FACTURA_CREAR',         'Generar facturas',                    'FACTURA'),
    ('FACTURA_ANULAR',        'Anular facturas',                     'FACTURA'),
    ('INVENTARIO_LEER',       'Consultar kardex de inventario',      'INVENTARIO'),
    ('DEUDOR_LEER',           'Consultar cartera de deudores',       'DEUDOR'),
    ('DEUDOR_ABONAR',         'Registrar abonos a deudores',         'DEUDOR'),
    ('REPORTE_LEER',          'Consultar reportes',                  'REPORTE'),
    ('USUARIO_LEER',          'Consultar usuarios',                  'USUARIO'),
    ('USUARIO_CREAR',         'Crear usuarios',                      'USUARIO'),
    ('USUARIO_EDITAR',        'Editar usuarios',                     'USUARIO'),
    ('USUARIO_ELIMINAR',      'Desactivar/eliminar usuarios',        'USUARIO'),
    ('ROL_GESTIONAR',         'Gestionar roles y permisos',          'ROL'),
    ('CONFIGURACION_LEER',    'Consultar configuracion del sistema', 'CONFIGURACION'),
    ('CONFIGURACION_EDITAR',  'Editar configuracion del sistema',    'CONFIGURACION');

-- ----------------------------------------------------------------------------
-- Asignacion: ADMIN recibe todos los permisos existentes
-- ----------------------------------------------------------------------------
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r
CROSS JOIN permiso p
WHERE r.nombre = 'ADMIN';

-- ----------------------------------------------------------------------------
-- Asignacion: VENDEDOR solo factura y consulta deudores (segun especificacion)
-- ----------------------------------------------------------------------------
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r
CROSS JOIN permiso p
WHERE r.nombre = 'VENDEDOR'
  AND p.codigo IN (
      'DASHBOARD_LEER',
      'PRODUCTO_LEER',
      'CLIENTE_LEER',
      'FACTURA_LEER',
      'FACTURA_CREAR',
      'DEUDOR_LEER',
      'DEUDOR_ABONAR'
  );
