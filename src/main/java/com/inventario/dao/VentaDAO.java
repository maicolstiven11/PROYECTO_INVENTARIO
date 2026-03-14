package com.inventario.dao;

// =====================================================================
// IMPORTACIONES NECESARIAS
// =====================================================================
import com.inventario.model.DetalleVenta; // Modelo que representa cada línea de producto vendido (ver: model/DetalleVenta.java)
import com.inventario.model.Venta;        // Modelo que representa la cabecera de una venta (ver: model/Venta.java)
import com.inventario.util.Conexion;      // Clase utilitaria para conectar a MySQL (ver: util/Conexion.java)
import java.sql.Connection;               // Conexión abierta con la base de datos
import java.sql.PreparedStatement;        // Consulta SQL segura con parámetros (?)
import java.sql.ResultSet;               // Resultados de un SELECT
import java.sql.SQLException;            // Errores de SQL
import java.sql.Statement;              // Para obtener claves generadas
import java.util.List;                   // Interfaz de lista

/**
 * DAO: Clase VentaDAO (Data Access Object)
 * 
 * Esta clase maneja TODAS las operaciones de las tablas VENTA y DETALLE_VENTA en MySQL.
 * También modifica INVENTARIO_DETALLE al restar stock cuando se vende un producto.
 * 
 * QUIÉN LA USA:
 * - VentaServlet.java: Para registrar ventas (action=finalizar), listar ventas (action=listar),
 *   y ver detalle de una venta específica (action=ver_detalle)
 * 
 * TABLAS QUE MANEJA: VENTA, DETALLE_VENTA, INVENTARIO_DETALLE (resta stock)
 */
public class VentaDAO {

    /**
     * 1. REGISTRAR VENTA COMPLETA (Cabecera + Detalles + Resta de Stock)
     * 
     * QUIÉN LO LLAMA: VentaServlet.finalizarVenta() → Cuando el usuario presiona "Finalizar Venta" en agregar_venta.jsp
     * QUÉ RECIBE:
     *   - Venta venta: Objeto con idInventario (de sesión), totalVenta (calculado), fechaVenta (fecha actual)
     *   - List<DetalleVenta> detalles: El carrito completo (lista de productos con cantidad, precio, subtotal)
     *     Los detalles vienen de: session.getAttribute("carrito") → agregado pieza por pieza en VentaServlet.agregarProducto()
     * QUÉ RETORNA: true si TODO se guardó correctamente, false si algo falló
     * 
     * TRANSACCIÓN ATÓMICA (3 operaciones que deben funcionar TODAS o NINGUNA):
     * 1. INSERT en VENTA (cabecera)
     * 2. INSERT en DETALLE_VENTA (cada producto vendido)
     * 3. UPDATE en INVENTARIO_DETALLE (restar stock de cada producto vendido)
     */
    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        Connection con = null;
        PreparedStatement psVenta = null;    // Para insertar en tabla VENTA
        PreparedStatement psDetalle = null;  // Para insertar en tabla DETALLE_VENTA
        PreparedStatement psStock = null;    // Para restar stock en INVENTARIO_DETALLE
        PreparedStatement psBuscar = null;   // Para buscar el id_detalle en INVENTARIO_DETALLE
        ResultSet rsKeys = null;             // Para obtener el ID auto-generado de la venta
        ResultSet rsBuscar = null;           // Para leer el resultado de la búsqueda
        boolean estatus = false;

        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // INICIAR TRANSACCIÓN: Todo o nada

            // =====================================================================
            // PASO 1: INSERTAR CABECERA DE VENTA en tabla VENTA
            // =====================================================================
            String sqlVenta = "INSERT INTO VENTA (id_inventario, total_venta, fecha_venta) VALUES (?, ?, ?)";
            psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS); // Pedimos ID generado
            psVenta.setInt(1, venta.getIdInventario());    // ? #1 ← Modelo: venta.getIdInventario() ← Servlet: session.getAttribute("idInventarioActual")
            psVenta.setDouble(2, venta.getTotalVenta());   // ? #2 ← Modelo: venta.getTotalVenta() ← Servlet: calcularTotal(carrito) (suma de subtotales)
            psVenta.setDate(3, venta.getFechaVenta());     // ? #3 ← Modelo: venta.getFechaVenta() ← Servlet: new Date(System.currentTimeMillis())

            int filas = psVenta.executeUpdate(); // Ejecuta el INSERT
            if (filas == 0) {
                throw new SQLException("Error al insertar la venta, no se crearon filas.");
            }

            // RECUPERAR ID GENERADO automáticamente por MySQL (AUTO_INCREMENT)
            rsKeys = psVenta.getGeneratedKeys();
            int idVentaGenerado = -1;
            if (rsKeys.next()) {
                idVentaGenerado = rsKeys.getInt(1); // El primer campo es el id_venta generado
            } else {
                throw new SQLException("Error al insertar la venta, no se obtuvo el ID.");
            }

            // =====================================================================
            // PASO 2: INSERTAR CADA DETALLE DE VENTA + RESTAR STOCK
            // Por cada producto en el carrito:
            //   a) Buscar su id_detalle en INVENTARIO_DETALLE (vía id_producto + id_inventario)
            //   b) Insertar en DETALLE_VENTA con ese id_inv_detalle
            //   c) Restar la cantidad vendida del stock en INVENTARIO_DETALLE
            // =====================================================================
            
            // SQL para insertar detalle: Usa id_inv_detalle (FK a INVENTARIO_DETALLE, no directamente a PRODUCTO)
            String sqlDetalle = "INSERT INTO DETALLE_VENTA (id_venta, id_inv_detalle, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            psDetalle = con.prepareStatement(sqlDetalle);

            // SQL para buscar el id_detalle correspondiente en INVENTARIO_DETALLE
            String sqlBuscar = "SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_inventario = ? AND id_producto = ?";
            psBuscar = con.prepareStatement(sqlBuscar);

            // SQL para RESTAR stock: cantidad_inicial = cantidad_inicial - cantidadVendida
            String sqlRestar = "UPDATE INVENTARIO_DETALLE SET cantidad_inicial = cantidad_inicial - ? WHERE id_detalle = ?";
            psStock = con.prepareStatement(sqlRestar);

            // Recorrer cada producto del carrito (cada DetalleVenta de la lista)
            for (DetalleVenta det : detalles) {
                
                // PASO 2a: Buscar el id_detalle en INVENTARIO_DETALLE para este producto
                psBuscar.setInt(1, venta.getIdInventario()); // ? #1 ← ID del inventario activo (de la sesión)
                psBuscar.setInt(2, det.getIdProducto());      // ? #2 ← ID del producto (auxiliar del carrito, viene de agregar_venta.jsp select)
                rsBuscar = psBuscar.executeQuery();

                int idInvDetalle = -1;
                if (rsBuscar.next()) {
                    idInvDetalle = rsBuscar.getInt("id_detalle"); // Obtenemos el ID del registro de stock
                } else {
                    throw new SQLException("Producto con ID " + det.getIdProducto() + " no encontrado en el inventario activo.");
                }

                // PASO 2b: Insertar detalle de venta con el id_inv_detalle encontrado
                psDetalle.setInt(1, idVentaGenerado);          // ? #1 ← ID de la venta recién creada
                psDetalle.setInt(2, idInvDetalle);             // ? #2 ← FK a INVENTARIO_DETALLE (encontrado en paso 2a)
                psDetalle.setInt(3, det.getCantidad());        // ? #3 ← Cantidad vendida. Viene de: carrito → item.getCantidad()
                psDetalle.setDouble(4, det.getPrecioUnitario());// ? #4 ← Precio unitario. Viene de: carrito → item.getPrecioUnitario()
                psDetalle.setDouble(5, det.getSubtotal());     // ? #5 ← Subtotal = cantidad × precio. Viene de: carrito → item.getSubtotal()
                psDetalle.addBatch(); // Acumula el INSERT (se ejecuta todo junto al final)

                // PASO 2c: RESTAR stock del inventario
                psStock.setInt(1, det.getCantidad());          // ? #1 ← Cuántas unidades restar
                psStock.setInt(2, idInvDetalle);               // ? #2 ← De qué registro de stock restar
                psStock.addBatch(); // Acumula el UPDATE
            }

            psDetalle.executeBatch(); // Ejecuta TODOS los INSERTs de detalle de una vez
            psStock.executeBatch();   // Ejecuta TODOS los UPDATEs de stock de una vez

            // =====================================================================
            // PASO 3: CONFIRMAR TRANSACCIÓN
            // Si llegamos aquí, TODO funcionó. Confirmamos los cambios en la BD.
            // =====================================================================
            con.commit();
            estatus = true;
            System.out.println("Venta registrada con éxito. ID: " + idVentaGenerado + " | Stock actualizado.");

        } catch (SQLException e) {
            System.err.println("Error en transacción de venta: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); // Si ALGO falló, DESHACER TODOS los cambios (venta, detalles y stock)
                    System.out.println("Se realizó Rollback de la venta.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // Cerrar TODOS los recursos para evitar fugas de conexiones
            try {
                if (rsBuscar != null) rsBuscar.close();
                if (rsKeys != null) rsKeys.close();
                if (psBuscar != null) psBuscar.close();
                if (psStock != null) psStock.close();
                if (psVenta != null) psVenta.close();
                if (psDetalle != null) psDetalle.close();
                if (con != null) {
                    con.setAutoCommit(true); // Restaurar modo auto-commit
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return estatus; // true = venta guardada con éxito, false = falló
    }

    /**
     * 2. LISTAR VENTAS POR NEGOCIO
     * 
     * QUIÉN LO LLAMA: VentaServlet.processRequest(action=listar) → Para mostrar historial de ventas
     * QUÉ RECIBE: int idNegocio → ID del negocio actual (viene de: session.getAttribute("idNegocioActual"))
     * QUÉ RETORNA: Lista de objetos Venta ordenados por fecha descendente (más reciente primero)
     * QUÉ HACE EN LA BD: SELECT de VENTA con JOIN a INVENTARIO para filtrar por negocio
     * 
     * DESTINO FINAL: request.setAttribute("listaVentas", lista) → visualizar_ventas.jsp → ${venta.totalVenta}
     */
    public java.util.List<Venta> listarVentas(int idNegocio) {
        java.util.List<Venta> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // JOIN con INVENTARIO necesario porque VENTA tiene FK a INVENTARIO, no directamente a NEGOCIO
            // Así filtramos: solo ventas de inventarios que pertenecen a ESTE negocio
            String sql = "SELECT v.* FROM VENTA v " +
                         "INNER JOIN INVENTARIO i ON v.id_inventario = i.id_inventario " + // Venta → Inventario
                         "WHERE i.id_negocio = ? " +                                       // Inventario → Negocio específico
                         "ORDER BY v.fecha_venta DESC";                                    // Más recientes primero
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio); // ? ← ID del negocio actual (de la sesión)
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));          // Columna BD → Modelo. Usado en JSP: ${venta.idVenta}
                v.setIdInventario(rs.getInt("id_inventario"));// Columna BD → Modelo. Para referencia interna
                v.setTotalVenta(rs.getDouble("total_venta")); // Columna BD → Modelo. Usado en JSP: ${venta.totalVenta}
                v.setFechaVenta(rs.getDate("fecha_venta"));   // Columna BD → Modelo. Usado en JSP: ${venta.fechaVenta}
                lista.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; // Retorna al VentaServlet → se pone en request → llega a visualizar_ventas.jsp
    }

    /**
     * 3. LISTAR DETALLE DE UNA VENTA ESPECÍFICA
     * 
     * QUIÉN LO LLAMA: VentaServlet.processRequest(action=ver_detalle) → Para ver qué productos se vendieron
     * QUÉ RECIBE: int idVenta → ID de la venta a consultar (viene de: request.getParameter("id_venta"))
     * QUÉ RETORNA: Lista de DetalleVenta con el nombre del producto incluido
     * QUÉ HACE EN LA BD:
     *   - SELECT de DETALLE_VENTA
     *   - JOIN con INVENTARIO_DETALLE para obtener id_producto
     *   - JOIN con PRODUCTO para obtener el nombre del producto
     * 
     * DESTINO FINAL: request.setAttribute("listaDetalles", lista) → detalle_venta.jsp → ${det.nombreProducto}
     */
    public java.util.List<DetalleVenta> listarDetalleVenta(int idVenta) {
        java.util.List<DetalleVenta> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // JOIN doble: DETALLE_VENTA → INVENTARIO_DETALLE → PRODUCTO
            // Esto es necesario porque DETALLE_VENTA tiene FK a INVENTARIO_DETALLE (id_inv_detalle),
            // y INVENTARIO_DETALLE tiene FK a PRODUCTO (id_producto)
            String sql = "SELECT d.*, p.nombre FROM DETALLE_VENTA d " +
                         "INNER JOIN INVENTARIO_DETALLE id ON d.id_inv_detalle = id.id_detalle " + // Detalle → Stock
                         "INNER JOIN PRODUCTO p ON id.id_producto = p.id_producto " +              // Stock → Producto (nombre)
                         "WHERE d.id_venta = ?";                                                   // Filtrar por venta específica
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idVenta); // ? ← ID de la venta a consultar
            rs = ps.executeQuery();
            
            while (rs.next()) {
                DetalleVenta d = new DetalleVenta();
                d.setIdDetalleVenta(rs.getInt("id_detalle_venta")); // Columna BD → Modelo
                d.setIdVenta(rs.getInt("id_venta"));                // Columna BD → Modelo
                d.setIdInvDetalle(rs.getInt("id_inv_detalle"));     // FK a INVENTARIO_DETALLE
                d.setCantidad(rs.getInt("cantidad"));               // Cantidad vendida. Usado en JSP: ${det.cantidad}
                d.setPrecioUnitario(rs.getDouble("precio_unitario"));// Precio al momento de la venta. Usado: ${det.precioUnitario}
                d.setSubtotal(rs.getDouble("subtotal"));            // Subtotal de esta línea. Usado: ${det.subtotal}
                d.setNombreProducto(rs.getString("nombre"));        // Nombre del producto (del JOIN). Usado: ${det.nombreProducto}
                
                lista.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar detalle venta: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; // Retorna al VentaServlet → se pone en request → llega a detalle_venta.jsp
    }
}
