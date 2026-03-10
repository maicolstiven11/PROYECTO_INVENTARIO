package com.inventario.dao;

import com.inventario.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO para obtener datos de informes y estadísticas del negocio.
 * Calcula totales de ventas, gastos y pedidos para el dashboard de informes.
 */
public class InformeDAO {

    /**
     * Obtiene el total de ventas de un inventario específico.
     */
    public double obtenerTotalVentas(int idInventario) {
        double total = 0;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            String sql = "SELECT COALESCE(SUM(total_venta), 0) AS total FROM VENTA WHERE id_inventario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idInventario);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total ventas: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return total;
    }

    /**
     * Obtiene el total de gastos de un inventario específico.
     */
    public double obtenerTotalGastos(int idInventario) {
        double total = 0;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            String sql = "SELECT COALESCE(SUM(subtotal), 0) AS total FROM GASTO_DIARIO WHERE id_inventario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idInventario);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total gastos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return total;
    }

    /**
     * Obtiene el total de pedidos de un inventario específico.
     */
    public double obtenerTotalPedidos(int idInventario) {
        double total = 0;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            String sql = "SELECT COALESCE(SUM(total_pedido), 0) AS total FROM PEDIDOS_PROVEEDOR WHERE id_inventario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idInventario);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total pedidos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return total;
    }

    /**
     * Obtiene el número total de ventas de un inventario específico.
     */
    public int obtenerCantidadVentas(int idInventario) {
        int cantidad = 0;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            String sql = "SELECT COUNT(*) AS total FROM VENTA WHERE id_inventario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idInventario);
            rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar ventas: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return cantidad;
    }
}
