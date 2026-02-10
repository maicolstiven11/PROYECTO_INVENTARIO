package com.inventario.dao;

import com.inventario.util.Conexion;
import com.inventario.model.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // 1. MÉTODO PARA LISTAR TODOS LOS PRODUCTOS
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            String sql = "SELECT * FROM PRODUCTO"; 
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("id_producto"));
                p.setNombre(rs.getString("nombre"));
                p.setMarca(rs.getString("marca"));
                p.setPrecioUnitario(rs.getDouble("precio_unitario"));
                p.setTipo(rs.getString("tipo"));
                p.setImagen(rs.getString("imagen"));
                p.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                p.setCantidadMedida(rs.getString("cantidad_medida"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lista;
    }

    // 2. MÉTODO PARA REGISTRAR UN NUEVO PRODUCTO
    public boolean registrarProducto(Producto p) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean registrado = false;
        
        try {
            con = Conexion.getConexion();
            String sql = "INSERT INTO PRODUCTO (nombre, marca, precio_unitario, tipo, imagen, fecha_vencimiento, cantidad_medida) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getMarca());
            ps.setDouble(3, p.getPrecioUnitario());
            ps.setString(4, p.getTipo());
            ps.setString(5, p.getImagen());
            
            if (p.getFechaVencimiento() != null) {
                ps.setDate(6, p.getFechaVencimiento());
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            
            ps.setString(7, p.getCantidadMedida());
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                registrado = true;
            }
            
        } catch (SQLException e) {
            System.out.println("ERROR SQL AL REGISTRAR PRODUCTO: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("ErrorSQL: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return registrado;
    }

    // 3. MÉTODO PARA ELIMINAR UN PRODUCTO (CON ELIMINACIÓN EN CASCADA)
    public boolean eliminarProducto(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;
        
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // Iniciar transacción
            
            // PASO 1: Eliminar de DETALLE_VENTA (si el producto fue vendido)
            String sqlDetalleVenta = "DELETE FROM DETALLE_VENTA WHERE id_producto = ?";
            ps = con.prepareStatement(sqlDetalleVenta);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            
            // PASO 2: Eliminar de INVENTARIO_DETALLE (si fue parte de un inventario)
            String sqlDetalleInv = "DELETE FROM INVENTARIO_DETALLE WHERE id_producto = ?";
            ps = con.prepareStatement(sqlDetalleInv);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            
            // PASO 3: Finalmente, eliminar el PRODUCTO
            String sqlProducto = "DELETE FROM PRODUCTO WHERE id_producto = ?";
            ps = con.prepareStatement(sqlProducto);
            ps.setInt(1, id);
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                eliminado = true;
            }
            
            con.commit(); // Confirmar todos los cambios
            System.out.println("Producto ID " + id + " eliminado correctamente con sus dependencias.");
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // Deshacer si algo falla
            } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return eliminado;
    }
    // 4. MÉTODO PARA OBTENER UN PRODUCTO POR ID (Para Ventas)
    public Producto obtenerProducto(int id) {
        Producto p = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            String sql = "SELECT * FROM PRODUCTO WHERE id_producto = ?"; 
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                p = new Producto();
                p.setIdProducto(rs.getInt("id_producto"));
                p.setNombre(rs.getString("nombre"));
                p.setMarca(rs.getString("marca"));
                p.setPrecioUnitario(rs.getDouble("precio_unitario"));
                p.setTipo(rs.getString("tipo"));
                p.setImagen(rs.getString("imagen"));
                p.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                p.setCantidadMedida(rs.getString("cantidad_medida"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return p;
    }
    
    // 5. MÉTODO PARA ACTUALIZAR UN PRODUCTO
    public boolean actualizarProducto(Producto p) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizado = false;
        
        try {
            con = Conexion.getConexion();
            String sql = "UPDATE PRODUCTO SET nombre = ?, marca = ?, precio_unitario = ?, " +
                         "tipo = ?, imagen = ?, fecha_vencimiento = ?, cantidad_medida = ? " +
                         "WHERE id_producto = ?";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getMarca());
            ps.setDouble(3, p.getPrecioUnitario());
            ps.setString(4, p.getTipo());
            ps.setString(5, p.getImagen());
            
            if (p.getFechaVencimiento() != null) {
                ps.setDate(6, p.getFechaVencimiento());
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            
            ps.setString(7, p.getCantidadMedida());
            ps.setInt(8, p.getIdProducto());
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                actualizado = true;
                System.out.println("Producto ID " + p.getIdProducto() + " actualizado correctamente.");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return actualizado;
    }
}
