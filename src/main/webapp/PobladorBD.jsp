<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="java.sql.Connection" %>
        <%@ page import="java.sql.Statement" %>
            <%@ page import="java.sql.SQLException" %>
                <%@ page import="com.inventario.util.Conexion" %>
                    <!DOCTYPE html>
                    <html>

                    <head>
                        <meta charset="UTF-8">
                        <title>Reseteo y Poblado de BD</title>
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                margin: 40px;
                            }

                            .success {
                                color: green;
                            }

                            .error {
                                color: red;
                            }
                        </style>
                    </head>

                    <body>
                        <h2>Inicializando Base de Datos (10 Registros por Tabla)</h2>
                        <% try { Connection con=Conexion.getConexion(); if (con !=null) { Statement
                            stmt=con.createStatement(); stmt.execute("SET FOREIGN_KEY_CHECKS=0;"); out.println("<p><b>1.
                                Chequeos Desactivados.</b></p>");
                            out.println("<p><b>2. Vaciando tablas antiguas...</b></p>");

                            String[] tables = new String[]{"DETALLE_VENTA", "VENTA", "DETALLE_PEDIDOS", "PEDIDO",
                            "GASTO", "INVENTARIO_DETALLE", "INVENTARIO", "PRODUCTO", "TIPO_PRODUCTO", "USUARIO_NEGOCIO",
                            "NEGOCIO", "TELEFONO_USUARIO", "CORREO_USUARIO", "USUARIO", "ROL", "DATOS_PROVEEDOR"};
                            for (String tbl : tables) {
                            stmt.execute("TRUNCATE TABLE " + tbl + ";");
                            }

                            out.println("<p><b>3. Insertando 10 registros por tabla...</b></p>");

                            stmt.execute("INSERT INTO ROL (id_rol, nombre_rol) VALUES (1, 'ADMIN'), (2,
                            'TRABAJADOR');");
                            stmt.execute("INSERT INTO USUARIO (id_usuario, id_rol, nombre, password) VALUES (1, 1,
                            'Admin Principal', 'admin123'), (2, 1, 'Admin Secundario', 'admin123'), (3, 1, 'Admin
                            Tercero', 'admin123'), (4, 2, 'Trabajador Luis', 'trab123'), (5, 2, 'Trabajador Ana',
                            'trab123'), (6, 2, 'Trabajador Pedro', 'trab123'), (7, 2, 'Trabajador Carlos', 'trab123'),
                            (8, 2, 'Trabajador Maria', 'trab123'), (9, 2, 'Trabajador Elena', 'trab123'), (10, 2,
                            'Trabajador Sofia', 'trab123');");
                            stmt.execute("INSERT INTO CORREO_USUARIO (id_correo, id_usuario, correo, estado) VALUES (1,
                            1, 'admin1@inventario.com', 'activo'), (2, 2, 'admin2@inventario.com', 'activo'), (3, 3,
                            'admin3@inventario.com', 'activo'), (4, 4, 'luis@inventario.com', 'activo'), (5, 5,
                            'ana@inventario.com', 'activo'), (6, 6, 'pedro@inventario.com', 'activo'), (7, 7,
                            'carlos@inventario.com', 'activo'), (8, 8, 'maria@inventario.com', 'activo'), (9, 9,
                            'elena@inventario.com', 'activo'), (10, 10, 'sofia@inventario.com', 'activo');");
                            stmt.execute("INSERT INTO NEGOCIO (id_negocio, nombre, direccion, estado) VALUES (1, 'Bar
                            Central', 'Calle 1', 'activo'), (2, 'Cantina El Paisa', 'Cra 5', 'activo'), (3, 'Taverna del
                            Abuelo', 'Av Principal', 'activo'), (4, 'Discoteca VIP', 'Calle 10', 'activo'), (5, 'Pub
                            Ingles', 'Zona T', 'activo'), (6, 'Rock Bar', 'Cra 15', 'activo'), (7, 'Bar Deportivo',
                            'Frente estadio', 'activo'), (8, 'Karaoke Feliz', 'Centro C', 'activo'), (9, 'Lounge Relax',
                            'Calle 94', 'activo'), (10, 'Taberna Irlandesa', 'Cra 7', 'activo');");
                            stmt.execute("INSERT INTO USUARIO_NEGOCIO (id_usuario, id_negocio) VALUES (1, 1), (1, 2),
                            (1, 3), (2, 4), (2, 5), (2, 6), (3, 7), (3, 8), (3, 9), (3, 10), (4, 1), (5, 2), (6, 3), (7,
                            4), (8, 5), (9, 6), (10, 7);");
                            stmt.execute("INSERT INTO TIPO_PRODUCTO (id_tipo, nombre_tipo) VALUES (1,
                            'CervezasNacionales'),(2, 'CervezasImportadas'),(3, 'Aguardiente'),(4, 'Ron'),(5,
                            'Whisky'),(6, 'Tequila'),(7, 'Gaseosas'),(8, 'Jugos'),(9, 'Pasabocas'),(10,
                            'Cigarrillos');");
                            stmt.execute("INSERT INTO PRODUCTO (id_producto, id_tipo, id_negocio, nombre, descripcion,
                            precio_unitario, estado) VALUES (1, 1, 1, 'Aguila', 'Botella', 4000, 'activo'), (2, 1, 1,
                            'Club Colombia', 'Botella', 5000, 'activo'), (3, 3, 1, 'Antioqueno', 'Garrafa', 65000,
                            'activo'), (4, 4, 1, 'Ron Caldas', 'Botella', 70000, 'activo'), (5, 5, 1, 'Bucanans',
                            'Botella', 210000, 'activo'), (6, 2, 1, 'Corona', 'Botella', 8000, 'activo'), (7, 7, 1,
                            'Coca Cola', 'Personal', 3500, 'activo'), (8, 8, 1, 'Hit Mora', 'Botella', 3000, 'activo'),
                            (9, 9, 1, 'Papas', 'Paquete', 2500, 'activo'), (10, 10, 1, 'Mustang', 'Cajetilla', 12000,
                            'activo');");
                            stmt.execute("INSERT INTO DATOS_PROVEEDOR (id_proveedor, nombre_proveedor, contacto,
                            telefono, correo) VALUES (1, 'Bavaria SA', 'Pedro V', '30011', 'v@bavaria.co'), (2,
                            'Postobon', 'Maria P', '30022', 'v@postobon.co'), (3, 'FLA', 'Carlos M', '30033',
                            'c@fla.co'), (4, 'Frito Lay', 'Ana G', '30044', 'a@fritolay.co'), (5, 'Dislicores', 'Lucia
                            F', '30055', 'v@dislicores.co'), (6, 'Distrib Elite', 'Omar S', '30066', 'o@elite.co'), (7,
                            'Hielos El Polo', 'Sofia F', '30077', 'p@hielo.co'), (8, 'Coca Cola F', 'Jorge R', '30088',
                            'r@femsa.co'), (9, 'Cigarreria', 'Diana C', '30099', 'd@ciga.co'), (10, 'Aba-Abarrotes',
                            'Luis P', '30000', 'l@abarrotes.co');");
                            stmt.execute("INSERT INTO INVENTARIO (id_inventario, id_negocio, fecha_inicio, tipo_control,
                            estado) VALUES (1, 1, '2023-01-01', 'mensual', 'inactivo'), (2, 1, '2023-02-01', 'mensual',
                            'inactivo'), (3, 1, '2023-03-01', 'mensual', 'inactivo'), (4, 1, '2023-04-01', 'mensual',
                            'inactivo'), (5, 1, '2023-05-01', 'mensual', 'inactivo'), (6, 1, '2023-06-01', 'mensual',
                            'inactivo'), (7, 1, '2023-07-01', 'mensual', 'inactivo'), (8, 1, '2023-08-01', 'mensual',
                            'inactivo'), (9, 1, '2023-09-01', 'mensual', 'inactivo'), (10, 1, '2023-10-01', 'semanal',
                            'activo');");
                            stmt.execute("INSERT INTO INVENTARIO_DETALLE (id_detalle, id_inventario, id_producto,
                            cantidad_inicial, cantidad_final) VALUES (1, 10, 1, 100, 0), (2, 10, 2, 80, 0), (3, 10, 3,
                            20, 0), (4, 10, 4, 15, 0), (5, 10, 5, 5, 0), (6, 10, 6, 50, 0), (7, 10, 7, 60, 0), (8, 10,
                            8, 40, 0), (9, 10, 9, 30, 0), (10, 10, 10, 10, 0);");
                            stmt.execute("INSERT INTO VENTA (id_venta, id_inventario, fecha, metodo_pago, total,
                            observaciones) VALUES (1, 10, '2023-10-01', 'Efectivo', 12000, 'Barra'), (2, 10,
                            '2023-10-01', 'Tarjeta', 25000, 'Mesa 1'), (3, 10, '2023-10-02', 'Transferencia', 65000,
                            'Mesa 2'), (4, 10, '2023-10-02', 'Efectivo', 8000, 'Llevar'), (5, 10, '2023-10-03',
                            'Efectivo', 210000, 'VIP'), (6, 10, '2023-10-03', 'Tarjeta', 70000, 'Mesa 3'), (7, 10,
                            '2023-10-04', 'Efectivo', 3500, 'Barra'), (8, 10, '2023-10-04', 'Transferencia', 85000,
                            'Mesa 5'), (9, 10, '2023-10-05', 'Efectivo', 15000, 'Mesa 6'), (10, 10, '2023-10-05',
                            'Tarjeta', 40000, 'Mesa 7');");
                            stmt.execute("INSERT INTO GASTO (id_gasto, id_inventario, fecha, categoria, monto,
                            observaciones) VALUES (1, 10, '2023-10-01', 'Recibos', 150000, 'Luz'), (2, 10, '2023-10-02',
                            'Nomina', 500000, 'Mesero'), (3, 10, '2023-10-03', 'Mantenimiento', 50000, 'Limpieza'), (4,
                            10, '2023-10-04', 'Insumos', 30000, 'Insumos'), (5, 10, '2023-10-05', 'Seguridad', 100000,
                            'Vigilante'), (6, 10, '2023-10-06', 'Recibos', 80000, 'Agua'), (7, 10, '2023-10-07',
                            'Marketing', 20000, 'Volantes'), (8, 10, '2023-10-08', 'Nomina', 600000, 'Admin'), (9, 10,
                            '2023-10-09', 'Insumos', 15000, 'Otros'), (10, 10, '2023-10-10', 'Otros', 10000,
                            'Transporte');");
                            stmt.execute("INSERT INTO PEDIDO (id_pedido, id_inventario, fecha_pedido, proveedor, total,
                            estado) VALUES (1, 10, '2023-10-01', 'Bavaria SA', 200000, 'entregado'), (2, 10,
                            '2023-10-02', 'Postobon', 150000, 'entregado'), (3, 10, '2023-10-03', 'FLA', 500000,
                            'entregado'), (4, 10, '2023-10-04', 'Frito Lay', 80000, 'entregado'), (5, 10, '2023-10-05',
                            'Dislicores', 400000, 'entregado'), (6, 10, '2023-10-06', 'Hielos El Polo', 50000,
                            'entregado'), (7, 10, '2023-10-07', 'Coca Cola Femsa', 120000, 'pendiente'), (8, 10,
                            '2023-10-08', 'Cigarreria', 90000, 'pendiente'), (9, 10, '2023-10-09', 'Distrib Elite',
                            250000, 'pendiente'), (10, 10, '2023-10-10', 'Aba-Abarrotes', 100000, 'pendiente');");

                            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
                            out.println("<p><b>4. Chequeos Reactivados.</b></p>");
                            out.println("<h3 class='success'>¡BASE DE DATOS POBLADA EXITOSAMENTE!</h3>");
                            out.println("<p>Todos los IDs han sido reseteados de 1 a 10 perfectamente alineados.</p>");
                            out.println("<a href='view/Inicio_sesion.html'>Volver al Login de Sistema</a>");

                            stmt.close();
                            con.close();
                            }
                            } catch (Exception e) {
                            out.println("<h3 class='error'>Error al resetear la Base de Datos</h3>");
                            out.println("
                            <pre>" + e.getMessage() + "</pre>");
                            }
                            %>
                    </body>

                    </html>