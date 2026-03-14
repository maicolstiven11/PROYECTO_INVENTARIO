package com.inventario.dao;

import com.inventario.util.Conexion;
import com.inventario.model.Proveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    // 1. LISTAR TODOS LOS PROVEEDORES
    public List<Proveedor> listarProveedores() {
        List<Proveedor> lista = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            String sql = "SELECT * FROM DATOS_PROVEEDOR";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setNombreProveedor(rs.getString("nombre_proveedor"));
                p.setContacto(rs.getString("contacto"));
                p.setTelefono(rs.getString("telefono"));
                p.setCorreo(rs.getString("correo"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar proveedores: " + e.getMessage());
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista;
    }

    // 2. REGISTRAR PROVEEDOR
    public boolean registrarProveedor(Proveedor p) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean registrado = false;
        
        try {
            con = Conexion.getConexion();
            String sql = "INSERT INTO DATOS_PROVEEDOR (nombre_proveedor, contacto, telefono, correo) VALUES (?, ?, ?, ?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombreProveedor());
            ps.setString(2, p.getContacto());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getCorreo());
            
            int filas = ps.executeUpdate();
            if (filas > 0) registrado = true;
            
        } catch (SQLException e) {
            System.out.println("Error SQL Proveedor: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("ErrorSQL: " + e.getMessage());
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return registrado;
    }
    // 3. ELIMINAR PROVEEDOR
    public boolean eliminarProveedor(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;
        
        try {
            con = Conexion.getConexion();
            // Intentar eliminar. Si hay pedidos asociados, saltará excepción por FK (lo cual es seguro)
            String sql = "DELETE FROM DATOS_PROVEEDOR WHERE id_proveedor = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                eliminado = true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return eliminado;
    }

    /**
     * Verifica si un proveedor ya existe por nombre o correo.
     * @param nombre Nombre del proveedor.
     * @param correo Correo del proveedor.
     * @return true si existe, false si no.
     */
    public boolean existeProveedor(String nombre, String correo) {
        boolean existe = false;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            String sql = "SELECT COUNT(*) FROM DATOS_PROVEEDOR WHERE nombre_proveedor = ? OR correo = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, correo);
            rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                existe = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar proveedor: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return existe;
    }
}
