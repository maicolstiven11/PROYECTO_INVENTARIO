package com.inventario.util;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class Poblador {
    
    public static String ejecutarPoblado() {
        Connection con = null;
        Statement stmt = null;
        StringBuilder log = new StringBuilder();
        
        try {
            con = Conexion.getConexion();
            if (con == null) {
                return "Error: No se pudo obtener la conexión a la base de datos.";
            }
            
            con.setAutoCommit(false);
            stmt = con.createStatement();
            
            // 1. Desactivar Foreign Keys temporalmente
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            log.append("<p>1. Chequeos Desactivados.</p>");
            
            // 2. Vaciando tablas antiguas
            String[] tables = {
                "detalle_venta", "venta", "detalle_pedidos", "pedidos_proveedor", "gasto_diario", 
                "inventario_detalle", "inventario", "producto", 
                "usuario_negocio", "negocio", "telefono_usuario", "correo_usuario", 
                "usuario", "rol", "datos_proveedor"
            };
            for (String tbl : tables) {
                stmt.execute("TRUNCATE TABLE " + tbl + ";");
            }
            log.append("<p>2. Vaciando tablas antiguas (TRUNCATE)... Completado.</p>");
            
            // 3. Insertando bloques de datos limpios
            log.append("<p>3. Insertando 10 registros por tabla con el esquema exacto...</p>");
            
            String[] inserts = {
                "INSERT INTO rol (id_rol, nombre_rol) VALUES (1, 'ADMIN'), (2, 'TRABAJADOR');",
                "INSERT INTO usuario (id_usuario, id_rol, nombre, password) VALUES " +
                    "(1, 1, 'Admin Principal', 'admin123'), (2, 1, 'Admin Secundario', 'admin123'), " +
                    "(3, 1, 'Admin Tercero', 'admin123'), (4, 2, 'Trabajador Luis', 'trab123'), " +
                    "(5, 2, 'Trabajador Ana', 'trab123'), (6, 2, 'Trabajador Pedro', 'trab123'), " +
                    "(7, 2, 'Trabajador Carlos', 'trab123'), (8, 2, 'Trabajador Maria', 'trab123'), " +
                    "(9, 2, 'Trabajador Elena', 'trab123'), (10, 2, 'Trabajador Sofia', 'trab123');",
                "INSERT INTO correo_usuario (id_correo, id_usuario, correo_electronico) VALUES " +
                    "(1, 1, 'admin1@inventario.com'), (2, 2, 'admin2@inventario.com'), " +
                    "(3, 3, 'admin3@inventario.com'), (4, 4, 'luis@inventario.com'), " +
                    "(5, 5, 'ana@inventario.com'), (6, 6, 'pedro@inventario.com'), " +
                    "(7, 7, 'carlos@inventario.com'), (8, 8, 'maria@inventario.com'), " +
                    "(9, 9, 'elena@inventario.com'), (10, 10, 'sofia@inventario.com');",
                "INSERT INTO telefono_usuario (id_telefono, id_usuario, numero_telefono) VALUES " +
                    "(1, 1, '3000000001'), (2, 2, '3000000002'), (3, 3, '3000000003'), " +
                    "(4, 4, '3000000004'), (5, 5, '3000000005'), (6, 6, '3000000006'), " +
                    "(7, 7, '3000000007'), (8, 8, '3000000008'), (9, 9, '3000000009'), (10, 10, '3000000010');",
                "INSERT INTO negocio (id_negocio, nombre, direccion, estado) VALUES " +
                    "(1, 'Bar Central', 'Calle 1', 'activo'), (2, 'Cantina El Paisa', 'Cra 5', 'activo'), " +
                    "(3, 'Taverna del Abuelo', 'Av Principal', 'activo'), (4, 'Discoteca VIP', 'Calle 10', 'activo'), " +
                    "(5, 'Pub Ingles', 'Zona T', 'activo'), (6, 'Rock Bar', 'Cra 15', 'activo'), " +
                    "(7, 'Bar Deportivo', 'Frente estadio', 'activo'), (8, 'Karaoke Feliz', 'Centro C', 'activo'), " +
                    "(9, 'Lounge Relax', 'Calle 94', 'activo'), (10, 'Taberna Irlandesa', 'Cra 7', 'activo');",
                "INSERT INTO usuario_negocio (id_usuario_negocio, id_usuario, id_negocio) VALUES " +
                    "(1, 1, 1), (2, 2, 2), (3, 3, 3), (4, 4, 4), (5, 5, 5), " +
                    "(6, 6, 6), (7, 7, 7), (8, 8, 8), (9, 9, 9), (10, 10, 10);",
                /* Intentar insertar string en ENUM, MySQL truncará si falla pero permite si es genérico. En su esquema es ENUM */
                "INSERT INTO producto (id_producto, nombre, marca, precio_unitario, tipo, imagen, fecha_vencimiento, cantidad_medida) VALUES " +
                    "(1, 'Aguila', 'Bavaria', 4000, 'bebida', 'img1.png', '2025-12-01', '330ml'), " +
                    "(2, 'Club Colombia', 'Bavaria', 5000, 'bebida', 'img2.png', '2025-12-01', '330ml'), " +
                    "(3, 'Antioqueno', 'FLA', 65000, 'bebida', 'img3.png', '2028-12-01', '1000ml'), " +
                    "(4, 'Ron Caldas', 'ILC', 70000, 'bebida', 'img4.png', '2029-12-01', '750ml'), " +
                    "(5, 'Bucanans', 'Diageo', 210000, 'bebida', 'img5.png', '2030-12-01', '750ml'), " +
                    "(6, 'Corona', 'Modelo', 8000, 'bebida', 'img6.png', '2025-06-01', '355ml'), " +
                    "(7, 'Coca Cola', 'Femsa', 3500, 'bebida', 'img7.png', '2024-12-01', '600ml'), " +
                    "(8, 'Hit Mora', 'Postobon', 3000, 'bebida', 'img8.png', '2024-08-01', '500ml'), " +
                    "(9, 'Papas', 'Frito Lay', 2500, 'snack', 'img9.png', '2024-05-01', '50g'), " +
                    "(10, 'Mustang', 'British T', 12000, 'cigarro', 'img10.png', '2026-01-01', '20und');",
                "INSERT INTO datos_proveedor (id_proveedor, nombre_proveedor, contacto, telefono, correo) VALUES " +
                    "(1, 'Bavaria SA', 'Pedro V', '30011', 'v@bavaria.co'), (2, 'Postobon', 'Maria P', '30022', 'v@postobon.co'), " +
                    "(3, 'FLA', 'Carlos M', '30033', 'c@fla.co'), (4, 'Frito Lay', 'Ana G', '30044', 'a@fritolay.co'), " +
                    "(5, 'Dislicores', 'Lucia F', '30055', 'v@dislicores.co'), (6, 'Distrib Elite', 'Omar S', '30066', 'o@elite.co'), " +
                    "(7, 'Hielos El Polo', 'Sofia F', '30077', 'p@hielo.co'), (8, 'Coca Cola F', 'Jorge R', '30088', 'r@femsa.co'), " +
                    "(9, 'Cigarreria', 'Diana C', '30099', 'd@ciga.co'), (10, 'Aba-Abarrotes', 'Luis P', '30000', 'l@abarrotes.co');",
                "INSERT INTO inventario (id_inventario, id_negocio, fecha_inicio, tipo_control, estado) VALUES " +
                    "(1, 1, '2023-01-01', 'mensual', 'inactivo'), (2, 1, '2023-02-01', 'mensual', 'inactivo'), " +
                    "(3, 1, '2023-03-01', 'mensual', 'inactivo'), (4, 1, '2023-04-01', 'mensual', 'inactivo'), " +
                    "(5, 1, '2023-05-01', 'mensual', 'inactivo'), (6, 1, '2023-06-01', 'mensual', 'inactivo'), " +
                    "(7, 1, '2023-07-01', 'mensual', 'inactivo'), (8, 1, '2023-08-01', 'mensual', 'inactivo'), " +
                    "(9, 1, '2023-09-01', 'mensual', 'inactivo'), (10, 1, '2023-10-01', 'semanal', 'activo');",
                "INSERT INTO inventario_detalle (id_detalle, id_inventario, id_producto, cantidad_inicial, cantidad_final) VALUES " +
                    "(1, 10, 1, 100, 0), (2, 10, 2, 80, 0), (3, 10, 3, 20, 0), (4, 10, 4, 15, 0), (5, 10, 5, 5, 0), " +
                    "(6, 10, 6, 50, 0), (7, 10, 7, 60, 0), (8, 10, 8, 40, 0), (9, 10, 9, 30, 0), (10, 10, 10, 10, 0);",
                "INSERT INTO venta (id_venta, id_inventario, total_venta, fecha_venta) VALUES " +
                    "(1, 10, 12000, '2023-10-01'), (2, 10, 25000, '2023-10-01'), " +
                    "(3, 10, 65000, '2023-10-02'), (4, 10, 8000, '2023-10-02'), " +
                    "(5, 10, 210000, '2023-10-03'), (6, 10, 70000, '2023-10-03'), " +
                    "(7, 10, 3500, '2023-10-04'), (8, 10, 85000, '2023-10-04'), " +
                    "(9, 10, 15000, '2023-10-05'), (10, 10, 40000, '2023-10-05');",
                "INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_inv_detalle, cantidad, precio_unitario, subtotal) VALUES " +
                    "(1, 1, 1, 3, 4000, 12000), (2, 2, 2, 5, 5000, 25000), (3, 3, 3, 1, 65000, 65000), " +
                    "(4, 4, 6, 1, 8000, 8000), (5, 5, 5, 1, 210000, 210000), (6, 6, 4, 1, 70000, 70000), " +
                    "(7, 7, 7, 1, 3500, 3500), (8, 8, 7, 24, 3541.67, 85000), (9, 9, 8, 5, 3000, 15000), " +
                    "(10, 10, 9, 16, 2500, 40000);",
                "INSERT INTO gasto_diario (id_gastos, id_inventario, cantidad, fecha, subtotal, descripcion) VALUES " +
                    "(1, 10, 1, '2023-10-01', 150000, 'Luz'), (2, 10, 1, '2023-10-02', 500000, 'Mesero'), " +
                    "(3, 10, 1, '2023-10-03', 50000, 'Limpieza'), (4, 10, 1, '2023-10-04', 30000, 'Insumos'), " +
                    "(5, 10, 1, '2023-10-05', 100000, 'Vigilante'), (6, 10, 1, '2023-10-06', 80000, 'Agua'), " +
                    "(7, 10, 1, '2023-10-07', 20000, 'Volantes'), (8, 10, 1, '2023-10-08', 600000, 'Admin'), " +
                    "(9, 10, 1, '2023-10-09', 15000, 'Otros'), (10, 10, 1, '2023-10-10', 10000, 'Transporte');",
                "INSERT INTO pedidos_proveedor (id_pedido_base, fecha_pedido, fecha_entrega, total_pedido, iva_pedido, subtotal, id_inventario, id_proveedor) VALUES " +
                    "(1, '2023-10-01', '2023-10-02', 200000, 31933, 168067, 10, 1), (2, '2023-10-02', '2023-10-03', 150000, 23950, 126050, 10, 2), " +
                    "(3, '2023-10-03', '2023-10-04', 500000, 79832, 420168, 10, 3), (4, '2023-10-04', '2023-10-05', 80000, 12773, 67227, 10, 4), " +
                    "(5, '2023-10-05', '2023-10-06', 400000, 63866, 336134, 10, 5), (6, '2023-10-06', '2023-10-07', 50000, 7983, 42017, 10, 6), " +
                    "(7, '2023-10-07', '2023-10-08', 120000, 19160, 100840, 10, 7), (8, '2023-10-08', '2023-10-09', 90000, 14370, 75630, 10, 8), " +
                    "(9, '2023-10-09', '2023-10-10', 250000, 39916, 210084, 10, 9), (10, '2023-10-10', '2023-10-11', 100000, 15966, 84034, 10, 10);",
                "INSERT INTO detalle_pedidos (id_pedido_registro, id_pedido_base, id_inv_detalle, cantidad_pedida, precio_unitario_real) VALUES " +
                    "(1, 1, 1, 50, 4000), (2, 2, 2, 30, 5000), (3, 3, 3, 7, 65000), " +
                    "(4, 4, 6, 10, 8000), (5, 5, 5, 1, 210000), (6, 6, 4, 1, 70000), " +
                    "(7, 7, 7, 34, 3500), (8, 8, 7, 25, 3500), (9, 9, 8, 83, 3000), " +
                    "(10, 10, 9, 40, 2500);"
            };
            
            for (String q : inserts) {
                stmt.execute(q);
            }
            
            // 4. Reactivar Foreign Keys
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            log.append("<p>4. Reactivando FK... Completado.</p>");
            
            con.commit();
            return log.toString() + "<h3 class='success'>¡BASE DE DATOS POBLADA EXITOSAMENTE!</h3><p>Todos los IDs han sido reseteados de 1 a 10 con su esquema oficial.</p>";

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch(SQLException sqle) {}
            return "<h3 class='error'>Error SQL: " + e.getMessage() + "</h3>";
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {}
        }
    }
}
