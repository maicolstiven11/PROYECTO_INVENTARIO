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
 * que el negocio les compra productos para abastecerse. (Crear, listar,
 * eliminar).
 */
public class ProveedorDAO {

    /**
     * Trae una lista completa con todos los distribuidores (proveedores) inscritos
     * en tabla.
     */
    /**
     * Trae una lista completa con todos los distribuidores (proveedores) inscritos
     * en tabla.
     */
    public List<Proveedor> listarProveedores() {
        List<Proveedor> lista = new ArrayList<>(); // Bolsa para guardar las empresas proveedoras
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion(); // Enlace a la bd

            // CONSULTA SQL (Listado Simple):
            // Selecciona absolutamente todo de la tabla DATOS_PROVEEDOR (nombre, contacto,
            // tel, correo).
            String sql = "SELECT * FROM DATOS_PROVEEDOR";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery(); // Pedimos los datos del servidor

            while (rs.next()) { // Recorremos fila por fila mientras MySQL tenga registros
                Proveedor p = new Proveedor(); // Fabricamos el objeto Java
                // Mapeamos los campos de la tabla a las variables del objeto:
                p.setIdProveedor(rs.getInt("id_proveedor")); // ID único
                p.setNombreProveedor(rs.getString("nombre_proveedor")); // Nombre de la empresa
                p.setContacto(rs.getString("contacto")); // Persona encargada
                p.setTelefono(rs.getString("telefono")); // Línea de atención
                p.setCorreo(rs.getString("correo")); // Email comercial
                lista.add(p); // Lo metemos en nuestra lista final
            }
        } catch (SQLException e) {
            System.err.println("Error al listar proveedores: " + e.getMessage());
        } finally {
            try {
                // Suicidio de recursos para evitar que la memoria se llene
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lista; // Enviamos la lista al Servlet/JSP
    }

    /**
     * Introduce a la base de datos a un nuevo proveedor con sus datos de contacto.
     */
    public boolean registrarProveedor(Proveedor p) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean registrado = false; // Bandera de victoria

        try {
            con = Conexion.getConexion(); // Nos alineamos con MySQL

            // CONSULTA SQL (Inserción de Proveedor):
            String sql = "INSERT INTO DATOS_PROVEEDOR (nombre_proveedor, contacto, telefono, correo) VALUES (?, ?, ?, ?)";

            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombreProveedor()); // Empresa
            ps.setString(2, p.getContacto()); // Persona
            ps.setString(3, p.getTelefono()); // Número
            ps.setString(4, p.getCorreo()); // Email

            int filasModificadas = ps.executeUpdate(); // Inyectamos físicamente los datos
            if (filasModificadas > 0) {
                registrado = true; // Si MySQL aceptó al nuevo proveedor
            }
        } catch (SQLException e) {
            System.out.println("Error falló insertar Proveedor: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error grave guardando proveedor en DB: " + e.getMessage());
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return registrado; // Notificamos si se guardó
    }

    /**
     * Intenta eliminar un proveedor completo con ID dado.
     * Cuidado: Fallará si ya existen facturas de compra (pedidos) asociadas a él.
     */
    public boolean eliminarProveedor(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;

        try {
            con = Conexion.getConexion(); // Nos conectamos

            // CONSULTA SQL (Borrado):
            String sql = "DELETE FROM DATOS_PROVEEDOR WHERE id_proveedor = ?";

            ps = con.prepareStatement(sql);
            ps.setInt(1, id); // Apuntamos al ID de la empresa a borrar

            int filas = ps.executeUpdate(); // Ordenamos el borrado
            if (filas > 0) {
                eliminado = true; // Éxito si se borró
            }
        } catch (SQLException e) {
            // NOTA DE SEGURIDAD: MySQL arrojará error aquí si el proveedor tiene Pedidos
            // asociados (Integridad Referencial).
            System.err.println(
                    "No se puede eliminar ese proveedor (Quizás tiene facturas pendientes): " + e.getMessage());
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return eliminado; // Informamos el resultado
    }

    /**
     * Módulo que verifica si un proveedor con el mismo Nombre o Correo ya existe.
     * Evita que se registren duplicados que confundan la contabilidad.
     */
    public boolean existeProveedor(String nombre, String correo) {
        boolean existe = false; // Empezamos asumiendo que es nuevo
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion(); // Unirse a MySQL

            // CONSULTA SQL (Búsqueda de Choque):
            // Cuenta registros que coincidan con el nombre O el correo (Cualquiera de los
            // dos detecta el clon).
            String sql = "SELECT COUNT(*) FROM DATOS_PROVEEDOR WHERE nombre_proveedor = ? OR correo = ?";

            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, correo);
            rs = ps.executeQuery(); // Pedimos el conteo

            if (rs.next() && rs.getInt(1) > 0) { // Si el conteo es 1 o más
                existe = true; // Encontramos un duplicado
            }
        } catch (SQLException e) {
            System.err.println("Problema averiguación duplicados proveedores: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return existe; // Sí ya estaba alguien así (boolean)
    }
}
