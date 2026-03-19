package com.inventario.dao;

import com.inventario.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase InformeDAO: Controlador Analítico Transaccional Estructural.
 * Representa una Capa Data Access Object exclusiva para la ejecución
 * de sentencias DQL abstractas (Select Count/Sum). 
 * Produce tipos de datos primitivos encapsulando lógica de Base de Datos.
 */
public class InformeDAO {

    /**
     * Módulo Getter de Subrutina SQL Analítica.
     * Retorna una propiedad estructurada tipo de dato nativo (double) de sumatorias. 
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
     * Método Accessor Matemático (Getter de sumatoria).
     * Aplica la interface limitante (idInventario parameter property) sobre consultas SQL complejas.
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
     * Accionador Abstracto Analítico Relacional (Getter).
     * Obtiene una sumatoria transaccional empaquetada en tipo valor de variable flotante iterativo.
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
     * Algoritmo Consultor Enumerador Analítico de Patrón (Counter DAO).
     * Interfaz con ResultSet Count de parámetros unificados. Retorna un Tipo Primitivo Int Abstracto.
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
