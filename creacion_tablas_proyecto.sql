-- SCRIPT DE CREACIÓN DE BASE DE DATOS Y TABLAS
-- PROYECTO: Contabilidad Sistemática / Inventario Bar
-- BASE DE DATOS: proyecto_inventario_bar

DROP DATABASE IF EXISTS proyecto_inventario_bar;
CREATE DATABASE proyecto_inventario_bar;
USE proyecto_inventario_bar;

-- ==========================================
-- 1. TABLAS DE SEGURIDAD Y USUARIOS
-- ==========================================

CREATE TABLE permiso (
    id_permiso INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(100) NOT NULL
);

CREATE TABLE rol_permiso (
    id_rol INT,
    id_permiso INT,
    PRIMARY KEY (id_rol, id_permiso),
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol) ON DELETE CASCADE,
    FOREIGN KEY (id_permiso) REFERENCES permiso(id_permiso) ON DELETE CASCADE
);

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_rol INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    password VARCHAR(200) NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

CREATE TABLE correo_usuario (
    id_correo INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    correo_electronico VARCHAR(150) NOT NULL UNIQUE,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE telefono_usuario (
    id_telefono INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    numero_telefono VARCHAR(20),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- ==========================================
-- 2. TABLAS DE NEGOCIO Y CATÁLOGO
-- ==========================================

CREATE TABLE negocio (
    id_negocio INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(200),
    estado VARCHAR(20) DEFAULT 'Activo'
);

CREATE TABLE usuario_negocio (
    id_usuario_negocio INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_negocio INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_negocio) REFERENCES negocio(id_negocio) ON DELETE CASCADE
);

CREATE TABLE producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    marca VARCHAR(100),
    precio_unitario DECIMAL(10, 2) NOT NULL,
    tipo VARCHAR(50),
    imagen VARCHAR(255),
    fecha_vencimiento DATE,
    cantidad_medida VARCHAR(50)
);

-- ==========================================
-- 3. TABLAS DE INVENTARIO Y STOCK
-- ==========================================

CREATE TABLE inventario (
    id_inventario INT AUTO_INCREMENT PRIMARY KEY,
    id_negocio INT NOT NULL,
    fecha_inicio DATE,
    tipo_control VARCHAR(50),
    estado VARCHAR(20) DEFAULT 'Activo',
    FOREIGN KEY (id_negocio) REFERENCES negocio(id_negocio) ON DELETE CASCADE
);

CREATE TABLE inventario_detalle (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_inventario INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad_inicial INT NOT NULL DEFAULT 0,
    cantidad_final INT,
    FOREIGN KEY (id_inventario) REFERENCES inventario(id_inventario) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto) ON DELETE CASCADE
);

-- ==========================================
-- 4. TABLAS DE MOVIMIENTOS (VENTAS Y GASTOS)
-- ==========================================

CREATE TABLE venta (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_inventario INT NOT NULL,
    total_venta DECIMAL(10, 2) NOT NULL,
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_inventario) REFERENCES inventario(id_inventario) ON DELETE CASCADE
);

CREATE TABLE detalle_venta (
    id_detalle_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    id_inv_detalle INT NOT NULL,  -- Conecta directo con el stock
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    FOREIGN KEY (id_inv_detalle) REFERENCES inventario_detalle(id_detalle)
);

CREATE TABLE gasto_diario (
    id_gasto INT AUTO_INCREMENT PRIMARY KEY,
    id_inventario INT NOT NULL,
    descripcion TEXT,
    cantidad INT NOT NULL,
    fecha DATE NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_inventario) REFERENCES inventario(id_inventario) ON DELETE CASCADE
);

-- ==========================================
-- 5. TABLAS DE PROVEEDORES Y PEDIDOS
-- ==========================================

CREATE TABLE datos_proveedor (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre_proveedor VARCHAR(150) NOT NULL,
    contacto VARCHAR(150),
    telefono VARCHAR(20),
    correo VARCHAR(150)
);

CREATE TABLE pedidos_proveedor (
    id_pedido_base INT AUTO_INCREMENT PRIMARY KEY,
    id_inventario INT NOT NULL,
    id_proveedor INT NOT NULL,
    fecha_pedido DATE NOT NULL,
    fecha_entrega DATE,
    subtotal DECIMAL(10, 2) NOT NULL,
    iva_pedido DECIMAL(10, 2),
    total_pedido DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_inventario) REFERENCES inventario(id_inventario) ON DELETE CASCADE,
    FOREIGN KEY (id_proveedor) REFERENCES datos_proveedor(id_proveedor)
);

CREATE TABLE detalle_pedidos (
    id_pedido_registro INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido_base INT NOT NULL,
    id_producto INT NOT NULL,
    id_inv_detalle INT NOT NULL, -- Conecta directo con el stock
    cantidad_pedido INT NOT NULL,
    precio_unitario_real DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_pedido_base) REFERENCES pedidos_proveedor(id_pedido_base) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    FOREIGN KEY (id_inv_detalle) REFERENCES inventario_detalle(id_detalle)
);

-- ==========================================
-- 6. REGISTROS INICIALES POR DEFECTO
-- ==========================================

-- Insertar roles por defecto
INSERT INTO rol (id_rol, nombre_rol) VALUES (1, 'ADMIN');
INSERT INTO rol (id_rol, nombre_rol) VALUES (2, 'TRABAJADOR');

-- Insertar permisos básicos
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (1, 'VER_BARES', 'Permite ver la lista de bares asignados');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (2, 'AGREGAR_NEGOCIO', 'Permite registrar nuevos bares');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (3, 'ELIMINAR_NEGOCIO', 'Permite eliminar bares (Admin)');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (4, 'GESTIONAR_TRABAJADORES', 'Permite crear y editar usuarios');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (5, 'VER_PRODUCTOS', 'Permite ver el listado de productos');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (6, 'AGREGAR_PRODUCTO', 'Permite crear nuevos productos');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (7, 'EDITAR_PRODUCTO', 'Permite editar productos');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (8, 'ELIMINAR_PRODUCTO', 'Permite eliminar productos');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (9, 'INICIAR_INVENTARIO', 'Permite iniciar ciclos de inventario');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (10, 'REALIZAR_VENTA', 'Permite registrar ventas');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (11, 'VER_HISTORIAL_VENTAS', 'Permite ver el historial completo de ventas');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (12, 'REGISTRAR_GASTO', 'Permite registrar gastos');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (13, 'VER_GASTOS', 'Permite ver historial de gastos');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (14, 'GESTIONAR_PROVEEDORES', 'Permite agregar y editar proveedores');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (15, 'HACER_PEDIDOS_PROVEEDOR', 'Permite realizar pedidos a proveedores');
INSERT INTO permiso (id_permiso, nombre, descripcion) VALUES (16, 'VER_INFORMES', 'Permite ver informes y reportes del sistema');

-- Asignar permisos a roles (1=ADMIN, 2=TRABAJADOR)
INSERT INTO rol_permiso (id_rol, id_permiso) VALUES
(1, 1), (2, 1), (1, 2), (1, 3), (1, 4), (1, 5), (2, 5),
(1, 6), (1, 7), (1, 8), (1, 9), (2, 9), (1, 10), (2, 10),
(1, 11), (2, 11), (1, 12), (2, 12), (1, 13), (1, 14),
(1, 15), (2, 15), (1, 16);

