-- ============================================================================
-- V17: Datos semilla del modulo formula - catalogo de quimicos usados en las
-- recetas de planta, y las formulas "Costilla" y "Chuleta" (si el producto
-- correspondiente ya existe en el catalogo). Todo idempotente: se puede
-- correr sobre una base ya poblada sin duplicar filas ni fallar.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Catalogo de quimicos: se asegura que cada nombre exista (insert por
-- nombre, sin duplicar) con stock en cero; el stock real se carga despues
-- via entradas de inventario de insumos (fuera del alcance de este seed).
-- ----------------------------------------------------------------------------
INSERT INTO insumo_quimico (nombre, unidad_medida, stock_actual, activo)
VALUES
    ('Sal nitral',              'KG', 0, true),
    ('Carragenina 3292',        'KG', 0, true),
    ('Carragenina 5239',        'KG', 0, true),
    ('Tripolifosfato',          'KG', 0, true),
    ('Bensopro (EMBAC)',        'KG', 0, true),
    ('Proteína Supra',          'KG', 0, true),
    ('Almidón de trigo',        'KG', 0, true),
    ('Goma xantana',            'KG', 0, true),
    ('Excelpro',                'KG', 0, true),
    ('Jamón California',        'KG', 0, true),
    ('Humo P-50',                'KG', 0, true),
    ('Eritorbato de sodio',     'KG', 0, true),
    ('Sal común',                'KG', 0, true),
    ('Saborizante tocineta',    'KG', 0, true),
    ('Adobo tocino',            'KG', 0, true),
    ('Fibragel MT',             'KG', 0, true),
    ('Azúcar morena',           'KG', 0, true),
    ('Saborizante concentrado', 'KG', 0, true),
    ('Adobo BBQ',               'KG', 0, true),
    ('Ácido láctico',           'KG', 0, true)
ON CONFLICT (nombre) DO NOTHING;

-- ----------------------------------------------------------------------------
-- Formula "Costilla" (cantidad_base = 100 KG). Solo se inserta si ya existe
-- un producto que haga match por nombre y ese producto aun no tiene formula.
-- La linea de "Agua potable" del origen (hoja de calculo) no es un insumo
-- quimico rastreado y se omite a proposito.
-- ----------------------------------------------------------------------------
DO $$
DECLARE
    v_producto_id BIGINT;
    v_producto_nombre VARCHAR;
    v_formula_id BIGINT;
BEGIN
    SELECT id, nombre INTO v_producto_id, v_producto_nombre
    FROM producto WHERE nombre ILIKE '%costilla%' LIMIT 1;

    IF v_producto_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM formula WHERE producto_id = v_producto_id) THEN
        INSERT INTO formula (producto_id, producto_nombre, cantidad_base, unidad_base, activo)
        VALUES (v_producto_id, v_producto_nombre, 100, 'KG', true)
        RETURNING id INTO v_formula_id;

        INSERT INTO detalle_formula (formula_id, insumo_id, numero, cantidad)
        SELECT v_formula_id, i.id, d.numero, d.cantidad
        FROM (VALUES
            (1,  'Sal nitral',              0.25),
            (2,  'Carragenina 5239',        0.4),
            (3,  'Tripolifosfato',          2),
            (4,  'Bensopro (EMBAC)',        0.5),
            (5,  'Proteína Supra',          0.3),
            (6,  'Almidón de trigo',        0.3),
            (10, 'Humo P-50',                0.05),
            (11, 'Eritorbato de sodio',     0.6),
            (12, 'Sal común',                0.6),
            (16, 'Azúcar morena',           1.33339),
            (17, 'Saborizante concentrado', 0.25),
            (18, 'Adobo BBQ',               0.1),
            (19, 'Ácido láctico',           0.02)
        ) AS d(numero, nombre_insumo, cantidad)
        JOIN insumo_quimico i ON i.nombre = d.nombre_insumo;
    END IF;
END $$;

-- ----------------------------------------------------------------------------
-- Formula "Chuleta" (cantidad_base = 3 KG). Solo se inserta si ya existe un
-- producto que haga match por nombre y ese producto aun no tiene formula.
--
-- NOTA PARA EL DUENIO DEL NEGOCIO: la hoja de calculo original de esta receta
-- tenia un bug de arrastre de formula (multiplicacion en cascada) que
-- generaba valores basura en las columnas de cantidad. Los valores de abajo
-- se reconstruyeron aplicando el mismo patron "porcentaje x base de agua"
-- que se verifico correcto en la hoja de Costilla (base de agua = 3 KG para
-- esta receta). Por favor revisar y confirmar estos valores contra la receta
-- fisica de planta antes de confiar en ellos para produccion real.
-- ----------------------------------------------------------------------------
DO $$
DECLARE
    v_producto_id BIGINT;
    v_producto_nombre VARCHAR;
    v_formula_id BIGINT;
BEGIN
    SELECT id, nombre INTO v_producto_id, v_producto_nombre
    FROM producto WHERE nombre ILIKE '%chuleta%' LIMIT 1;

    IF v_producto_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM formula WHERE producto_id = v_producto_id) THEN
        INSERT INTO formula (producto_id, producto_nombre, cantidad_base, unidad_base, activo)
        VALUES (v_producto_id, v_producto_nombre, 3, 'KG', true)
        RETURNING id INTO v_formula_id;

        INSERT INTO detalle_formula (formula_id, insumo_id, numero, cantidad)
        SELECT v_formula_id, i.id, d.numero, d.cantidad
        FROM (VALUES
            (1,  'Sal nitral',              0.024),
            (2,  'Carragenina 3292',        0.015),
            (3,  'Tripolifosfato',          0.09576),
            (4,  'Bensopro (EMBAC)',        0.01092),
            (5,  'Proteína Supra',          0.03),
            (6,  'Almidón de trigo',        0.05457),
            (7,  'Goma xantana',            0.00123),
            (8,  'Excelpro',                0.021),
            (9,  'Jamón California',        0.02655),
            (10, 'Humo P-50',                0.00159),
            (11, 'Eritorbato de sodio',     0.03),
            (12, 'Sal común',                0.05457),
            (13, 'Saborizante tocineta',    0.0159),
            (14, 'Adobo tocino',            0.00531),
            (15, 'Fibragel MT',             0.03)
        ) AS d(numero, nombre_insumo, cantidad)
        JOIN insumo_quimico i ON i.nombre = d.nombre_insumo;
    END IF;
END $$;
