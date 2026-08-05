/**
 * Modulo de formulas (recetas) de produccion: define cuanto de cada quimico
 * (insumo) se necesita para producir un producto, y permite "producir" una
 * cantidad del producto descontando automaticamente los quimicos del
 * inventario de insumos (FEFO, por lote) y registrando la entrada
 * correspondiente al inventario del producto. Los nombres de los quimicos
 * se ocultan a los usuarios sin el permiso {@code INSUMO_VER_NOMBRE} (por
 * ejemplo, el rol PLANTA): solo ven el numero de frasco.
 */
package com.eldiamante360.formula;
