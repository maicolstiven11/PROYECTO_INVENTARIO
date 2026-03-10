package com.inventario.dao;

import com.inventario.model.DetalleVenta;
import com.inventario.model.Venta;
import com.inventario.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class VentaDAO {

    /**
     * Registra una venta completa, sus detalles y RESTA STOCK automáticamente.
     * CAMBIADO: Ahora usa id_inv_detalle en vez de id_producto.
     * NUEVO: Resta la cantidad vendida de inventario_detalle.cantidad_inicial.
     * 
     * Flujo de la transacción:
     * 1. Insertar cabecera en VENTA
     * 2. Para cada producto vendido:
     *    a. Buscar el id_detalle en INVENTARIO_DETALLE que corresponda al producto+inventario
     *    b. Insertar en DETALLE_VENTA con id_inv_detalle
     *    c. RESTAR la cantidad vendida del stock (cantidad_inicial)
     * 3. Commit o Rollback
     */
    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        Connection con = null;
        PreparedStatement psVenta = null;
        PreparedStatement psDetalle = null;
        PreparedStatement psStock = null;
        PreparedStatement psBuscar = null;
        ResultSet rsKeys = null;
        ResultSet rsBuscar = null;
        boolean estatus = false;

        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // INICIAR TRANSACCIÓN

            // 1. INSERTAR CABECERA DE VENTA
            String sqlVenta = "INSERT INTO VENTA (id_inventario, total_venta, fecha_venta) VALUES (?, ?, ?)";
            psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setInt(1, venta.getIdInventario());
            psVenta.setDouble(2, venta.getTotalVenta());
            psVenta.setDate(3, venta.getFechaVenta());

            int filas = psVenta.executeUpdate();
            if (filas == 0) {
                throw new SQLException("Error al insertar la venta, no se crearon filas.");
            }

            // RECUPERAR ID GENERADO
            rsKeys = psVenta.getGeneratedKeys();
            int idVentaGenerado = -1;
            if (rsKeys.next()) {
                idVentaGenerado = rsKeys.getInt(1);
            } else {
                throw new SQLException("Error al insertar la venta, no se obtuvo el ID.");
            }

            // 2. INSERTAR DETALLES Y RESTAR STOCK
            // CAMBIADO: Ahora usa id_inv_detalle en vez de id_producto
            String sqlDetalle = "INSERT INTO DETALLE_VENTA (id_venta, id_inv_detalle, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            psDetalle = con.prepareStatement(sqlDetalle);

            // SQL para buscar el id_detalle en inventario_detalle por producto e inventario
            String sqlBuscar = "SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_inventario = ? AND id_producto = ?";
            psBuscar = con.prepareStatement(sqlBuscar);

            // SQL para RESTAR stock
            String sqlRestar = "UPDATE INVENTARIO_DETALLE SET cantidad_inicial = cantidad_inicial - ? WHERE id_detalle = ?";
            psStock = con.prepareStatement(sqlRestar);

            for (DetalleVenta det : detalles) {
                // 2a. Buscar el id_detalle en INVENTARIO_DETALLE
                psBuscar.setInt(1, venta.getIdInventario());
                psBuscar.setInt(2, det.getIdProducto()); // idProducto auxiliar del carrito
                rsBuscar = psBuscar.executeQuery();

                int idInvDetalle = -1;
                if (rsBuscar.next()) {
                    idInvDetalle = rsBuscar.getInt("id_detalle");
                } else {
                    throw new SQLException("Producto con ID " + det.getIdProducto() + " no encontrado en el inventario activo.");
                }

                // 2b. Insertar detalle de venta con id_inv_detalle
                psDetalle.setInt(1, idVentaGenerado);
                psDetalle.setInt(2, idInvDetalle);         // CAMBIADO: id_inv_detalle
                psDetalle.setInt(3, det.getCantidad());
                psDetalle.setDouble(4, det.getPrecioUnitario());
                psDetalle.setDouble(5, det.getSubtotal());
                psDetalle.addBatch();

                // 2c. RESTAR stock del inventario
                psStock.setInt(1, det.getCantidad());      // Cuánto restar
                psStock.setInt(2, idInvDetalle);            // De cuál registro
                psStock.addBatch();
            }

            psDetalle.executeBatch(); // Ejecutar inserts
            psStock.executeBatch();   // Ejecutar restas de stock

            // CONFIRMAR TRANSACCIÓN
            con.commit();
            estatus = true;
            System.out.println("Venta registrada con éxito. ID: " + idVentaGenerado + " | Stock actualizado.");

        } catch (SQLException e) {
            System.err.println("Error en transacción de venta: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                    System.out.println("Se realizó Rollback de la venta.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (rsBuscar != null) rsBuscar.close();
                if (rsKeys != null) rsKeys.close();
                if (psBuscar != null) psBuscar.close();
                if (psStock != null) psStock.close();
                if (psVenta != null) psVenta.close();
                if (psDetalle != null) psDetalle.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return estatus;
    }

    // LISTAR VENTAS POR NEGOCIO (sin cambios en la query)
    public java.util.List<Venta> listarVentas(int idNegocio) {
        java.util.List<Venta> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            String sql = "SELECT v.* FROM VENTA v " +
                         "INNER JOIN INVENTARIO i ON v.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ? " +
                         "ORDER BY v.fecha_venta DESC";
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));
                v.setIdInventario(rs.getInt("id_inventario"));
                v.setTotalVenta(rs.getDouble("total_venta"));
                v.setFechaVenta(rs.getDate("fecha_venta"));
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
        return lista;
    }

    /**
     * LISTAR DETALLE DE UNA VENTA ESPECÍFICA
     * CAMBIADO: Ahora hace JOIN con INVENTARIO_DETALLE y luego con PRODUCTO
     * para obtener el nombre del producto a través de id_inv_detalle
     */
    public java.util.List<DetalleVenta> listarDetalleVenta(int idVenta) {
        java.util.List<DetalleVenta> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // CAMBIADO: JOIN con INVENTARIO_DETALLE -> PRODUCTO para obtener nombre
            String sql = "SELECT d.*, p.nombre FROM DETALLE_VENTA d " +
                         "INNER JOIN INVENTARIO_DETALLE id ON d.id_inv_detalle = id.id_detalle " +
                         "INNER JOIN PRODUCTO p ON id.id_producto = p.id_producto " +
                         "WHERE d.id_venta = ?";
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idVenta);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                DetalleVenta d = new DetalleVenta();
                d.setIdDetalleVenta(rs.getInt("id_detalle_venta"));
                d.setIdVenta(rs.getInt("id_venta"));
                d.setIdInvDetalle(rs.getInt("id_inv_detalle")); // CAMBIADO
                d.setCantidad(rs.getInt("cantidad"));
                d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                d.setSubtotal(rs.getDouble("subtotal"));
                d.setNombreProducto(rs.getString("nombre"));
                
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
        return lista;
    }
}
