package com.inventario.dao;

import com.inventario.model.Gasto;    
import com.inventario.util.Conexion;  
import java.sql.Connection;           
import java.sql.PreparedStatement;    
import java.sql.ResultSet;           
import java.sql.SQLException;        

/**
 * Clase GastoDao.
 * 
 * Se encarga de gestionar el guardado y consulta de los gastos diarios 
 * asociados a un inventario en la base de datos.
 */
public class GastoDao {

    /**
     * Guarda un nuevo gasto en la base de datos.
     * Recibe los datos del gasto y los inserta en la tabla GASTO_DIARIO.
     */
    public boolean registrarGasto(Gasto g) throws SQLException{
        Connection con = null; // Inicializamos la conexión vacía
        PreparedStatement ps = null; // Preparamos el objeto para la consulta SQL
        boolean registrado = false; // Variable para confirmar si se guardó bien
        
        try{
            con = Conexion.getConexion(); // Nos conectamos a la BD
            // Consulta para insertar en la tabla GASTO_DIARIO los 5 campos principales
            String sql = "INSERT INTO GASTO_DIARIO (id_inventario, cantidad, fecha, subtotal, descripcion) VALUES (?,?,?,?,?)";
            
            ps = con.prepareStatement(sql); // Preparamos la consulta
            
            ps.setInt(1, g.getId_inventario()); // Asignamos el ID del inventario al primer ?
            ps.setInt(2, g.getCantidad()); // Asignamos la cantidad al segundo ?
            ps.setDate(3, g.getFecha()); // Asignamos la fecha al tercer ?
            ps.setDouble(4, g.getSubtotal()); // Asignamos el subtotal (dinero) al cuarto ?
            ps.setString(5, g.getDescripcion()); // Asignamos el detalle del gasto al quinto ?
            
            if(ps.executeUpdate()>0){ // Ejecutamos. Si modifica 1 fila o más, fue un éxito
                registrado = true; // Confirmamos guardado
            }
        }catch (SQLException e){
            e.printStackTrace(); // Muestra el error en consola si la base de datos falla
        }finally {
            try {
                if (ps != null) ps.close(); // Cerramos la consulta para evitar fugas de memoria
                if (con != null) con.close(); // Cerramos la conexión a la base de datos
            } catch (SQLException ex) {
                ex.printStackTrace(); 
            }
        }
    
        return registrado; // Retornamos true si guardó o false si hubo error
    }

    /**
     * Lista todos los gastos registrados que pertenecen a un negocio en particular.
     * Para saber de qué negocio es el gasto, busca a través de la tabla INVENTARIO.
     */
    public java.util.List<Gasto> listarGastos(int idNegocio) {
        java.util.List<Gasto> lista = new java.util.ArrayList<>(); // Creamos la lista que devolveremos al final
        Connection con = null; // Variable de conexión
        PreparedStatement ps = null; // Variable de consulta SQL
        ResultSet rs = null; // Variable para los resultados devueltos por la BD
        
        try {
            con = Conexion.getConexion(); // Conectamos a la base de datos
            // Buscamos todos los campos de GASTO_DIARIO (g.*)
            // Se une (INNER JOIN) con la tabla INVENTARIO (i) porque el gasto conoce al inventario, y el inventario conoce al negocio
            // Así podemos filtrar por id_negocio y ordenar de lo más reciente a lo más viejo (DESC)
            String sql = "SELECT g.* FROM GASTO_DIARIO g " +
                         "INNER JOIN INVENTARIO i ON g.id_inventario = i.id_inventario " + 
                         "WHERE i.id_negocio = ? " +                                       
                         "ORDER BY g.fecha DESC";                                           
            
            ps = con.prepareStatement(sql); // Preparamos el SQL
            ps.setInt(1, idNegocio); // Le inyectamos el ID del negocio que estamos buscando
            rs = ps.executeQuery(); // Realizamos la consulta
            
            while (rs.next()) { // Recorremos fila por fila lo que respondió la BD
                Gasto g = new Gasto(); // Creamos un nuevo objeto Gasto
                g.setId_gastos(rs.getInt("id_gastos")); // Llenamos su ID de gasto
                g.setId_inventario(rs.getInt("id_inventario")); // Llenamos a qué inventario pertenece
                g.setCantidad(rs.getInt("cantidad")); // Llenamos cuántos items fueron
                g.setFecha(rs.getDate("fecha")); // Llenamos la fecha
                g.setSubtotal(rs.getDouble("subtotal")); // Llenamos el dinero gastado
                g.setDescripcion(rs.getString("descripcion")); // Llenamos el detalle de lo que fue
                
                lista.add(g); // Insertamos este objeto completo en nuestra lista
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Gastos: " + e.getMessage()); // Print de error en caso de fallo
        } finally {
            try {
                if (rs != null) rs.close(); // Limpieza del resultado
                if (ps != null) ps.close(); // Limpieza del SQL
                if (con != null) con.close(); // Cierre de BD
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; // Se retorna la lista llena de gastos
    }
}
