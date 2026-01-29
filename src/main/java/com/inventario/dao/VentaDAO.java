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
     * Registra una venta completa y sus detalles en una sola transacción.
     * @param venta Objeto Venta con los datos de cabecera
     * @param detalles Lista de detalles (productos) de la venta
     * @return true si se registró éxito, false si falló
     */
    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        Connection con = null;
        PreparedStatement psVenta = null;
        PreparedStatement psDetalle = null;
        ResultSet rsKeys = null;
        boolean estatus = false;

        try {
            con = Conexion.getConexion();
            // INICIAR TRANSACCIÓN
            con.setAutoCommit(false);

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

            // 2. INSERTAR DETALLES
            String sqlDetalle = "INSERT INTO DETALLE_VENTA (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            psDetalle = con.prepareStatement(sqlDetalle);

            for (DetalleVenta det : detalles) {
                psDetalle.setInt(1, idVentaGenerado);
                psDetalle.setInt(2, det.getIdProducto());
                psDetalle.setInt(3, det.getCantidad());
                psDetalle.setDouble(4, det.getPrecioUnitario());
                psDetalle.setDouble(5, det.getSubtotal());
                
                psDetalle.addBatch(); // Agregar al lote
            }

            psDetalle.executeBatch(); // Ejecutar lote completo

            // CONFIRMAR TRANSACCIÓN
            con.commit();
            estatus = true;
            System.out.println("Venta registrada con éxito. ID: " + idVentaGenerado);

        } catch (SQLException e) {
            System.err.println("Error en transacción de venta: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); // DESHACER CAMBIOS SI FALLA
                    System.out.println("Se realizó Rollback de la venta.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (rsKeys != null) rsKeys.close();
                if (psVenta != null) psVenta.close();
                if (psDetalle != null) psDetalle.close();
                if (con != null) {
                    con.setAutoCommit(true); // Restaurar autocommit
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return estatus;
    }

    // LISTAR VENTAS POR NEGOCIO
    public java.util.List<Venta> listarVentas(int idNegocio) {
        java.util.List<Venta> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // Unimos VENTA con INVENTARIO para filtrar por el negocio
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

    // LISTAR DETALLE DE UNA VENTA ESPECÍFICA
    public java.util.List<DetalleVenta> listarDetalleVenta(int idVenta) {
        java.util.List<DetalleVenta> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // Unimos DETALLE_VENTA con PRODUCTO para obtener el nombre
            String sql = "SELECT d.*, p.nombre FROM DETALLE_VENTA d " +
                         "INNER JOIN PRODUCTO p ON d.id_producto = p.id_producto " +
                         "WHERE d.id_venta = ?";
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idVenta);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                DetalleVenta d = new DetalleVenta();
                d.setIdDetalleVenta(rs.getInt("id_detalle_venta"));
                d.setIdVenta(rs.getInt("id_venta"));
                d.setIdProducto(rs.getInt("id_producto"));
                d.setCantidad(rs.getInt("cantidad"));
                d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                d.setSubtotal(rs.getDouble("subtotal"));
                // Seteamos el nombre (Transient field)
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
