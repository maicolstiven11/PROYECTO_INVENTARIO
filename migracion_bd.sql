-- ============================================================
-- SCRIPT DE MIGRACIÓN DE BASE DE DATOS (REVISADO)
-- Este script solo realiza los cambios estrictamente necesarios
-- para el manejo de stock, según las indicaciones del usuario.
-- ============================================================

-- ============================================================
-- PASO 1: TABLA DETALLE_VENTA
-- Cambiar id_producto por id_inv_detalle
-- ============================================================

-- Desactivar chequeo de llaves foráneas para permitir la modificación
SET FOREIGN_KEY_CHECKS = 0;

-- 1.1 Eliminar la llave foránea antigua
-- El usuario indicó que se llama: detalle_venta_ibfk_2
ALTER TABLE detalle_venta DROP FOREIGN KEY detalle_venta_ibfk_2;

-- 1.2 Renombrar la columna o reemplazarla
-- Para mantener los datos, es mejor renombrarla y luego asegurar que apunte a inventario_detalle
ALTER TABLE detalle_venta CHANGE COLUMN id_producto id_inv_detalle INT;

-- 1.3 Agregar la nueva llave foránea apuntando a inventario_detalle
ALTER TABLE detalle_venta ADD CONSTRAINT fk_detalle_venta_inv 
FOREIGN KEY (id_inv_detalle) REFERENCES inventario_detalle(id_detalle);

-- ============================================================
-- PASO 2: TABLA DETALLE_PEDIDOS
-- Cambiar id_producto por id_inv_detalle
-- ============================================================

-- 2.1 Eliminar la llave foránea antigua
-- El usuario indicó que se llama: detalle_pedidos_ibfk_2
ALTER TABLE detalle_pedidos DROP FOREIGN KEY detalle_pedidos_ibfk_2;

-- 2.2 Renombrar la columna
ALTER TABLE detalle_pedidos CHANGE COLUMN id_producto id_inv_detalle INT;

-- 2.3 Agregar la nueva llave foránea apuntando a inventario_detalle
ALTER TABLE detalle_pedidos ADD CONSTRAINT fk_detalle_pedidos_inv 
FOREIGN KEY (id_inv_detalle) REFERENCES inventario_detalle(id_detalle);

-- Reactivar chequeo de llaves foráneas
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- VERIFICACIÓN
-- ============================================================
DESCRIBE detalle_venta;
DESCRIBE detalle_pedidos;
