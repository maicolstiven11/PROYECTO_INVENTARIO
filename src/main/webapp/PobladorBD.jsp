<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="java.sql.Connection" %>
        <%@ page import="java.sql.Statement" %>
            <%@ page import="java.sql.SQLException" %>
                <%@ page import="com.inventario.util.Conexion" %>
                    <%@ page import="com.inventario.util.Cifrado" %>
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
                                stmt=con.createStatement(); stmt.execute("SET FOREIGN_KEY_CHECKS=0;"); out.println("<p>
                                <b>1. Chequeos Desactivados.</b></p>");
                                out.println("<p><b>2. Vaciando tablas antiguas...</b></p>");

                                String[] tables = new String[]{
                                "detalle_venta", "venta", "detalle_pedidos", "pedidos_proveedor",
                                "gasto_diario", "inventario_detalle", "inventario", "producto",
                                "usuario_negocio", "negocio", "telefono_usuario",
                                "correo_usuario", "usuario", "rol_permisos", "permiso", "rol", "datos_proveedor"
                                };

                                for (String tbl : tables) {
                                stmt.execute("TRUNCATE TABLE " + tbl + ";");
                                }

                                out.println("<p><b>3. Insertando 10 registros por tabla...</b></p>");

                                stmt.execute("INSERT INTO ROL (id_rol, nombre_rol) VALUES (1, 'ADMIN'), (2,
                                'TRABAJADOR');");

                                String qPermiso = "INSERT INTO PERMISO (id_permiso, nombre, descripcion) VALUES " +
                                "(1, 'VER_BARES', 'Permite ver la lista de bares asignados'), " +
                                "(2, 'AGREGAR_NEGOCIO', 'Permite registrar nuevos bares'), " +
                                "(3, 'ELIMINAR_NEGOCIO', 'Permite eliminar bares (Admin)'), " +
                                "(4, 'GESTIONAR_TRABAJADORES', 'Permite crear y editar usuarios'), " +
                                "(5, 'VER_PRODUCTOS', 'Permite ver el listado de productos'), " +
                                "(6, 'AGREGAR_PRODUCTO', 'Permite crear nuevos productos'), " +
                                "(7, 'EDITAR_PRODUCTO', 'Permite editar productos'), " +
                                "(8, 'ELIMINAR_PRODUCTO', 'Permite eliminar productos'), " +
                                "(9, 'INICIAR_INVENTARIO', 'Permite iniciar ciclos de inventario'), " +
                                "(10, 'REALIZAR_VENTA', 'Permite registrar ventas'), " +
                                "(11, 'VER_HISTORIAL_VENTAS', 'Permite ver el historial completo de ventas'), " +
                                "(12, 'REGISTRAR_GASTO', 'Permite registrar gastos'), " +
                                "(13, 'VER_GASTOS', 'Permite ver historial de gastos'), " +
                                "(14, 'GESTIONAR_PROVEEDORES', 'Permite agregar y editar proveedores'), " +
                                "(15, 'HACER_PEDIDOS_PROVEEDOR', 'Permite realizar pedidos a proveedores'), " +
                                "(16, 'VER_INFORMES', 'Permite ver informes y reportes del sistema');";
                                stmt.execute(qPermiso);

                                String qRolPerm = "INSERT INTO ROL_PERMISOS (id_rol, id_permiso) VALUES " +
                                "(1, 1), (2, 1), (1, 2), (1, 3), (1, 4), (1, 5), (2, 5), " +
                                "(1, 6), (1, 7), (1, 8), (1, 9), (2, 9), (1, 10), (2, 10), " +
                                "(1, 11), (2, 11), (1, 12), (2, 12), (1, 13), (1, 14), " +
                                "(1, 15), (2, 15), (1, 16);";
                                stmt.execute(qRolPerm);

                                String pAdmin = Cifrado.sha256("admin123");
                                String pTrab = Cifrado.sha256("trab123");
                                String qUser = "INSERT INTO USUARIO (id_usuario, id_rol, nombre, password) VALUES " +
                                "(1, 1, 'Admin Principal', '" + pAdmin + "'), (2, 1, 'Admin Secundario', '" + pAdmin +
                                "'), " +
                                "(3, 1, 'Admin Tercero', '" + pAdmin + "'), (4, 2, 'Trabajador Luis', '" + pTrab + "'),
                                " +
                                "(5, 2, 'Trabajador Ana', '" + pTrab + "'), (6, 2, 'Trabajador Pedro', '" + pTrab + "'),
                                " +
                                "(7, 2, 'Trabajador Carlos', '" + pTrab + "'), (8, 2, 'Trabajador Maria', '" + pTrab +
                                "'), " +
                                "(9, 2, 'Trabajador Elena', '" + pTrab + "'), (10, 2, 'Trabajador Sofia', '" + pTrab +
                                "');";
                                stmt.execute(qUser);

                                String qCorreo = "INSERT INTO CORREO_USUARIO (id_correo, id_usuario, correo_electronico)
                                VALUES " +
                                "(1, 1, 'admin1@inventario.com'), (2, 2, 'admin2@inventario.com'), " +
                                "(3, 3, 'admin3@inventario.com'), (4, 4, 'luis@inventario.com'), " +
                                "(5, 5, 'ana@inventario.com'), (6, 6, 'pedro@inventario.com'), " +
                                "(7, 7, 'carlos@inventario.com'), (8, 8, 'maria@inventario.com'), " +
                                "(9, 9, 'elena@inventario.com'), (10, 10, 'sofia@inventario.com');";
                                stmt.execute(qCorreo);

                                String qNegocio = "INSERT INTO NEGOCIO (id_negocio, nombre, direccion, estado) VALUES "
                                +
                                "(1, 'Bar Central', 'Calle 1', 'Activo'), (2, 'Cantina El Paisa', 'Cra 5', 'Activo'), "
                                +
                                "(3, 'Taverna del Abuelo', 'Av Principal', 'Activo'), (4, 'Discoteca VIP', 'Calle 10',
                                'Activo'), " +
                                "(5, 'Pub Ingles', 'Zona T', 'Activo'), (6, 'Rock Bar', 'Cra 15', 'Activo'), " +
                                "(7, 'Bar Deportivo', 'Frente estadio', 'Activo'), (8, 'Karaoke Feliz', 'Centro C',
                                'Activo'), " +
                                "(9, 'Lounge Relax', 'Calle 94', 'Activo'), (10, 'Taberna Irlandesa', 'Cra 7',
                                'Activo');";
                                stmt.execute(qNegocio);

                                String qUserNegocio = "INSERT INTO USUARIO_NEGOCIO (id_usuario, id_negocio) VALUES " +
                                "(1, 1), (1, 2), (1, 3), (2, 4), (2, 5), (2, 6), (3, 7), (3, 8), " +
                                "(3, 9), (3, 10), (4, 1), (5, 2), (6, 3), (7, 4), (8, 5), (9, 6), (10, 7);";
                                stmt.execute(qUserNegocio);

                                String qProd = "INSERT INTO PRODUCTO (id_producto, nombre, marca, precio_unitario, tipo,
                                imagen, cantidad_medida) VALUES " +
                                "(1, 'Aguila', 'Bavaria', 4000, 'bebida', '', 'Botella'), " +
                                "(2, 'Club Colombia', 'Bavaria', 5000, 'bebida', '', 'Botella'), " +
                                "(3, 'Antioqueno', 'FLA', 65000, 'bebida', '', 'Garrafa'), " +
                                "(4, 'Ron Caldas', 'ILC', 70000, 'bebida', '', 'Botella'), " +
                                "(5, 'Bucanans', 'Diageo', 210000, 'bebida', '', 'Botella'), " +
                                "(6, 'Corona', 'Modelo', 8000, 'bebida', '', 'Botella'), " +
                                "(7, 'Coca Cola', 'Femsa', 3500, 'bebida', '', 'Personal'), " +
                                "(8, 'Hit Mora', 'Postobon', 3000, 'bebida', '', 'Botella'), " +
                                "(9, 'Papas', 'Margarita', 2500, 'snack', '', 'Paquete'), " +
                                "(10, 'Mustang', 'Protabaco', 12000, 'cigarro', '', 'Cajetilla');";
                                stmt.execute(qProd);

                                String qProv = "INSERT INTO DATOS_PROVEEDOR (id_proveedor, nombre_proveedor, contacto,
                                telefono, correo) VALUES " +
                                "(1, 'Bavaria SA', 'Pedro V', '30011', 'v@bavaria.co'), (2, 'Postobon', 'Maria P',
                                '30022', 'v@postobon.co'), " +
                                "(3, 'FLA', 'Carlos M', '30033', 'c@fla.co'), (4, 'Frito Lay', 'Ana G', '30044',
                                'a@fritolay.co'), " +
                                "(5, 'Dislicores', 'Lucia F', '30055', 'v@dislicores.co'), (6, 'Distrib Elite', 'Omar
                                S', '30066', 'o@elite.co'), " +
                                "(7, 'Hielos El Polo', 'Sofia F', '30077', 'p@hielo.co'), (8, 'Coca Cola F', 'Jorge R',
                                '30088', 'r@femsa.co'), " +
                                "(9, 'Cigarreria', 'Diana C', '30099', 'd@ciga.co'), (10, 'Aba-Abarrotes', 'Luis P',
                                '30000', 'l@abarrotes.co');";
                                stmt.execute(qProv);

                                String qInv = "INSERT INTO INVENTARIO (id_inventario, id_negocio, fecha_inicio,
                                tipo_control, estado) VALUES " +
                                "(1, 1, '2023-01-01', 'mensual', 'inactivo'), (2, 1, '2023-02-01', 'mensual',
                                'inactivo'), " +
                                "(3, 1, '2023-03-01', 'mensual', 'inactivo'), (4, 1, '2023-04-01', 'mensual',
                                'inactivo'), " +
                                "(5, 1, '2023-05-01', 'mensual', 'inactivo'), (6, 1, '2023-06-01', 'mensual',
                                'inactivo'), " +
                                "(7, 1, '2023-07-01', 'mensual', 'inactivo'), (8, 1, '2023-08-01', 'mensual',
                                'inactivo'), " +
                                "(9, 1, '2023-09-01', 'mensual', 'inactivo'), (10, 1, '2023-10-01', 'semanal',
                                'activo');";
                                stmt.execute(qInv);

                                String qInvDet = "INSERT INTO INVENTARIO_DETALLE (id_detalle, id_inventario,
                                id_producto, cantidad_inicial, cantidad_final) VALUES " +
                                "(1, 10, 1, 100, 0), (2, 10, 2, 80, 0), (3, 10, 3, 20, 0), (4, 10, 4, 15, 0), " +
                                "(5, 10, 5, 5, 0), (6, 10, 6, 50, 0), (7, 10, 7, 60, 0), (8, 10, 8, 40, 0), " +
                                "(9, 10, 9, 30, 0), (10, 10, 10, 10, 0);";
                                stmt.execute(qInvDet);

                                String qVenta = "INSERT INTO VENTA (id_venta, id_inventario, total_venta, fecha_venta)
                                VALUES " +
                                "(1, 10, 12000, NOW()), (2, 10, 25000, NOW()), (3, 10, 65000, NOW()), (4, 10, 8000,
                                NOW()), (5, 10, 210000, NOW()), " +
                                "(6, 10, 70000, NOW()), (7, 10, 3500, NOW()), (8, 10, 85000, NOW()), (9, 10, 15000,
                                NOW()), (10, 10, 40000, NOW());";
                                stmt.execute(qVenta);

                                String qDetVenta = "INSERT INTO DETALLE_VENTA (id_detalle_venta, id_venta,
                                id_inv_detalle, cantidad, subtotal) VALUES " +
                                "(1, 1, 1, 3, 12000), (2, 2, 2, 5, 25000), (3, 3, 3, 1, 65000), " +
                                "(4, 4, 6, 1, 8000), (5, 5, 5, 1, 210000), (6, 6, 4, 1, 70000), " +
                                "(7, 7, 7, 1, 3500), (8, 8, 3, 1, 65000), (9, 9, 2, 3, 15000), " +
                                "(10, 10, 6, 5, 40000);";
                                stmt.execute(qDetVenta);

                                String qGasto = "INSERT INTO GASTO_DIARIO (id_gastos, id_inventario, descripcion,
                                cantidad, fecha, subtotal) VALUES " +
                                "(1, 10, 'Luz', 1, '2023-10-01', 150000), (2, 10, 'Mesero', 1, '2023-10-02', 500000), "
                                +
                                "(3, 10, 'Limpieza', 1, '2023-10-03', 50000), (4, 10, 'Insumos', 1, '2023-10-04',
                                30000), " +
                                "(5, 10, 'Vigilante', 1, '2023-10-05', 100000), (6, 10, 'Agua', 1, '2023-10-06', 80000),
                                " +
                                "(7, 10, 'Volantes', 1, '2023-10-07', 20000), (8, 10, 'Admin', 1, '2023-10-08', 600000),
                                " +
                                "(9, 10, 'Otros', 1, '2023-10-09', 15000), (10, 10, 'Transporte', 1, '2023-10-10',
                                10000);";
                                stmt.execute(qGasto);

                                String qPedido = "INSERT INTO PEDIDOS_PROVEEDOR (id_pedido_base, id_inventario,
                                id_proveedor, fecha_pedido, subtotal, total_pedido) VALUES " +
                                "(1, 10, 1, '2023-10-01', 200000, 200000), (2, 10, 2, '2023-10-02', 150000, 150000), " +
                                "(3, 10, 3, '2023-10-03', 500000, 500000), (4, 10, 4, '2023-10-04', 80000, 80000), " +
                                "(5, 10, 5, '2023-10-05', 400000, 400000), (6, 10, 6, '2023-10-06', 50000, 50000), " +
                                "(7, 10, 8, '2023-10-07', 120000, 120000), (8, 10, 9, '2023-10-08', 90000, 90000), " +
                                "(9, 10, 6, '2023-10-09', 250000, 250000), (10, 10, 10, '2023-10-10', 100000, 100000);";
                                stmt.execute(qPedido);

                                stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
                                out.println("<p><b>4. Chequeos Reactivados.</b></p>");
                                out.println("<h3 class='success'>¡BASE DE DATOS POBLADA EXITOSAMENTE!</h3>");
                                out.println("<p>Todos los IDs han sido reseteados de 1 a 10 perfectamente alineados.</p>
                                ");
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