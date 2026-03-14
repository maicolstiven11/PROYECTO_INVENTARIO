package com.inventario.dao;

import com.inventario.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DetalleInventarioDAO {

    /**
     * Registra el stock inicial de un producto en un inventario específico.
     */
    public boolean insertarDetalle(int idInventario, int idProducto, double cantidadInicial) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean registrado = false;
        
        try {
            con = Conexion.getConexion();
            String sql = "INSERT INTO INVENTARIO_DETALLE (id_inventario, id_producto, cantidad_inicial) VALUES (?, ?, ?)";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idInventario);
            ps.setInt(2, idProducto);
            ps.setDouble(3, cantidadInicial);
            
            int filas = ps.executeUpdate();
            registrado = (filas > 0);
            
        } catch (SQLException e) {
            System.err.println("Error al insertar detalle inventario: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return registrado;
    }

    /**
     * Lista todos los registros de detalle para un inventario específico.
     */
    public java.util.List<com.inventario.model.DetalleInventario> listarDetalles(int idInventario) {
        java.util.List<com.inventario.model.DetalleInventario> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            String sql = "SELECT d.*, p.nombre FROM INVENTARIO_DETALLE d " +
                         "JOIN PRODUCTO p ON d.id_producto = p.id_producto " +
                         "WHERE d.id_inventario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idInventario);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                com.inventario.model.DetalleInventario d = new com.inventario.model.DetalleInventario();
                d.setIdDetalle(rs.getInt("id_detalle"));
                d.setIdInventario(rs.getInt("id_inventario"));
                d.setIdProducto(rs.getInt("id_producto"));
                d.setCantidadInicial(rs.getDouble("cantidad_inicial"));
                d.setCantidadFinal(rs.getDouble("cantidad_final"));
                d.setNombreProducto(rs.getString("nombre"));
                lista.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar detalles inventario: " + e.getMessage());
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
     * Actualiza la cantidad física final de un producto al cerrar el inventario.
     */
    public boolean actualizarCantidadFinal(int idInventario, int idProducto, double cantidadFinal) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizado = false;
        
        try {
            con = Conexion.getConexion();
            String sql = "UPDATE INVENTARIO_DETALLE SET cantidad_final = ? WHERE id_inventario = ? AND id_producto = ?";
            ps = con.prepareStatement(sql);
            ps.setDouble(1, cantidadFinal);
            ps.setInt(2, idInventario);
            ps.setInt(3, idProducto);
            
            int filas = ps.executeUpdate();
            actualizado = (filas > 0);
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cantidad final: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return actualizado;
    }

    /**
     * Obtiene el stock actual (cantidad_inicial) de un producto en un inventario específico.
     */
    public double obtenerStockActual(int idInventario, int idProducto) {
        double stock = 0;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            String sql = "SELECT cantidad_inicial FROM INVENTARIO_DETALLE WHERE id_inventario = ? AND id_producto = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idInventario);
            ps.setInt(2, idProducto);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                stock = rs.getDouble("cantidad_inicial");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener stock actual: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return stock;
    }

    /**
     * Lista detalles de inventario con precio unitario del producto.
     * Se usa para calcular el descuadre monetario al cerrar inventario.
     */
    public java.util.List<com.inventario.model.DetalleInventario> listarDetallesConPrecio(int idInventario) {
        java.util.List<com.inventario.model.DetalleInventario> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            String sql = "SELECT d.*, p.nombre, p.precio_unitario " +
                         "FROM INVENTARIO_DETALLE d " +
                         "JOIN PRODUCTO p ON d.id_producto = p.id_producto " +
                         "WHERE d.id_inventario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idInventario);
            rs = ps.executeQuery();

            while (rs.next()) {
                com.inventario.model.DetalleInventario d = new com.inventario.model.DetalleInventario();
                d.setIdDetalle(rs.getInt("id_detalle"));
                d.setIdInventario(rs.getInt("id_inventario"));
                d.setIdProducto(rs.getInt("id_producto"));
                d.setCantidadInicial(rs.getDouble("cantidad_inicial"));
                d.setCantidadFinal(rs.getDouble("cantidad_final"));
                d.setNombreProducto(rs.getString("nombre"));
                d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                lista.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar detalles con precio: " + e.getMessage());
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
     * Obtiene el ID del detalle de inventario para un producto. 
     * Si el producto no existe en el inventario actual, lo inserta con cantidad 0,0 y devuelve el nuevo ID.
     */
    public int obtenerOCrearDetalle(int idInventario, int idProducto) {
        int idDetalle = -1;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            
            // 1. Intentar buscar
            String sqlBusqueda = "SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_inventario = ? AND id_producto = ?";
            ps = con.prepareStatement(sqlBusqueda);
            ps.setInt(1, idInventario);
            ps.setInt(2, idProducto);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                idDetalle = rs.getInt("id_detalle");
            } else {
                // 2. Si no existe, insertarlo con cantidad 0
                String sqlInsert = "INSERT INTO INVENTARIO_DETALLE (id_inventario, id_producto, cantidad_inicial, cantidad_final) VALUES (?, ?, 0, 0)";
                PreparedStatement psInsert = con.prepareStatement(sqlInsert, java.sql.Statement.RETURN_GENERATED_KEYS);
                psInsert.setInt(1, idInventario);
                psInsert.setInt(2, idProducto);
                psInsert.executeUpdate();
                
                ResultSet rsInsert = psInsert.getGeneratedKeys();
                if (rsInsert.next()) {
                    idDetalle = rsInsert.getInt(1);
                }
                rsInsert.close();
                psInsert.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener/crear detalle inventario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return idDetalle;
    }
}
