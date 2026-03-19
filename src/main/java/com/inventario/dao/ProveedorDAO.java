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
 * Clase ProveedorDAO.
 *
 * Módulo para gestionar los proveedores o empresas externas a los
 * que el negocio les compra productos para abastecerse. (Crear, listar, eliminar).
 */
public class ProveedorDAO {

    /**
     * Trae una lista completa con todos los distribuidores (proveedores) inscritos en tabla.
     */
    public List<Proveedor> listarProveedores() {
        List<Proveedor> lista = new ArrayList<>(); // Fabricar la lista colectora 
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion(); // Enlace a sql
            String sql = "SELECT * FROM DATOS_PROVEEDOR"; // Orden pedir todo a db
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery(); // Ejercicio de captura de renglones
            
            while (rs.next()) { // Reciclamos y pasamos del renglón 1, al 2...
                Proveedor p = new Proveedor(); // Creamos la estructura proveedora de base
                // Le vaciamos uno a uno la Data encontrada en el campo string del sql
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setNombreProveedor(rs.getString("nombre_proveedor"));
                p.setContacto(rs.getString("contacto")); // Su representante u nombre 2
                p.setTelefono(rs.getString("telefono")); // Telefono principal base celular
                p.setCorreo(rs.getString("correo")); // Correo base.
                lista.add(p); // Añadir a los registros en cola.
            }
        } catch (SQLException e) { // Fallo de listado en array
            System.err.println("Error al listar proveedores: " + e.getMessage());
        } finally { // Autolimpieza de ram
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; // Se le devuelve a la llamada externa de servlet
    }

    /**
     * Introduce a la base de datos de manera atómica a un nuevo proveedor usando su objeto con datos llenos
     */
    public boolean registrarProveedor(Proveedor p) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean registrado = false; // Flag de OK
        
        try {
            con = Conexion.getConexion(); // Nos alineamos servidor DB
            // Mandamos guardar en tabla el proveedor y sus 4 cajas basicas String
            String sql = "INSERT INTO DATOS_PROVEEDOR (nombre_proveedor, contacto, telefono, correo) VALUES (?, ?, ?, ?)";
            ps = con.prepareStatement(sql); // Empaquetar
            ps.setString(1, p.getNombreProveedor()); // Sellar paquete
            ps.setString(2, p.getContacto());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getCorreo());
            
            int filas = ps.executeUpdate(); // Requerir Inserción final física a disco!
            if (filas > 0) registrado = true; // Si el numero de renglones introducidos sobrepasa 0, se cantará victoria.
            
        } catch (SQLException e) { // Problemas de la gran bd
            System.out.println("Error falló insertar Proveedor: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error grave guardando prod DB: " + e.getMessage());
        } finally { // Depuración
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return registrado; // Mandar la noticia. 
    }
    
    /**
     * Intenta eliminar un proveedor completo con ID dado.
     * Cuidado, fallará si del proveedor ya teníamos creadas FACTURAS asociadas a PEDIDO_PROVEEDOR, por la llave foránea (Dependencia).
     */
    public boolean eliminarProveedor(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;
        
        try {
            con = Conexion.getConexion();
            // Delete al identificador
            String sql = "DELETE FROM DATOS_PROVEEDOR WHERE id_proveedor = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id); // Ponemos que local borrar 
            
            int filas = ps.executeUpdate(); // Lanzamos ejecución 
            if (filas > 0) { // Si lo encontró y lo voló
                eliminado = true; // Confirmamos Delete
            }
            
        } catch (SQLException e) { // Si falló
            // Normalmente lanza error si hay constraint fallido, entonces el Servlet mostrará en front un Alert "no se puede borrar".
            System.err.println("No se puede eliminar ese proveedor, quizás debas facturas de él: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return eliminado; // Responde con boolean al final
    }

    /**
     * Módulo que cuenta cuantos proveedores hay pero bajo tu mismo Nombre o Correo (Validación de choque/Duplicidad de ingreso de registros en formulario de Proveedor Servlet)
     */
    public boolean existeProveedor(String nombre, String correo) {
        boolean existe = false; // Como siempre partimos es Falso
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion(); // Unirse
            // Búsqueda cruzada, usa OR si hay alguno por nombre o por correo (Ambas detectan).
            String sql = "SELECT COUNT(*) FROM DATOS_PROVEEDOR WHERE nombre_proveedor = ? OR correo = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);   // Inyecta name a comparar
            ps.setString(2, correo);   // Inyecta Correo
            rs = ps.executeQuery(); // Reúso Resultset por la Count
            if (rs.next() && rs.getInt(1) > 0) { // Si hubo y dio mayor que nada...
                existe = true; // Confirmar la copia/clon
            }
        } catch (SQLException e) {
            System.err.println("Problema averiguación duplicados proveedores: " + e.getMessage()); // Console text
        } finally { // Libere recursos DB
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return existe; // Sí ya estaba alguien así (boolean)
    }
}
