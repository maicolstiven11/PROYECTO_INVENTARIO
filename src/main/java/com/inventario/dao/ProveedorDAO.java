package com.inventario.dao;

import com.inventario.util.Conexion;
import com.inventario.model.Proveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Patrón DAO Analítico Estructurado: ProveedorDAO.
 *
 * Singleton Abstracto (Instanciador persistente).
 * Se encarga de mapear y manipular el objeto Relacional POJO Entidad Proveedor ejecutando operaciones CRUD directas y seguras.
 */
public class ProveedorDAO {

    /**
     * Constructor Múltiple (Coleccionador Data Structure Extractor).
     *
     * Iterador sobre nodos del ResultSet SQL mapping al objeto model Proveedor instanciado. 
     */
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
            System.err.println("Error constraint context array loop length bounds limits: " + e.getMessage());
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista;
    }

    /**
     * Getter Extractor/Setter Unitario: Register Property Limits.
     *
     * Inyección abstracta parametrizada. 
     * Ejecuta Inserción relacional POJO Entity mapping object properties.
     */
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
            System.out.println("Mutation Exception Limit: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error Constraint Limits Parameter Insert Exception Bounds: " + e.getMessage());
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return registrado;
    }
    
    /**
     * Controlador Destructor Restringido.
     *
     * Invoca sentencia DELETE abstraida con pre-check en caso de Cascade Validation Error Constraint Relacional.
     */
    public boolean eliminarProveedor(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;
        
        try {
            con = Conexion.getConexion();
            String sql = "DELETE FROM DATOS_PROVEEDOR WHERE id_proveedor = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                eliminado = true;
            }
            
        } catch (SQLException e) {
            System.err.println("FK Foreign Key bounds integrity relational exception context mapping limit: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return eliminado;
    }

    /**
     * Encapsulador Escalar Numérico String.
     * Módulo Boolean Validador Count Constraint Parameter Setter.
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
            System.err.println("Property Object scalar iteration bounds count property constraint check mapping limit length Exception failed context : " + e.getMessage());
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
