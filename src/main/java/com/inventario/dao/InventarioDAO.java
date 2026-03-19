package com.inventario.dao;

import com.inventario.util.Conexion;      
import com.inventario.model.Inventario;   
import java.sql.Connection;               
import java.sql.PreparedStatement;        
import java.sql.ResultSet;               
import java.sql.SQLException;            
import java.sql.Date;                    

/**
 * Clase InventarioDAO.
 * 
 * Se dedica exclusivamente al registro de periodos de Inventario (iniciar/finalizar)
 * y a obtener datos de los inventarios de cada Negocio para organizar el sistema.
 */
public class InventarioDAO {

    /**
     * Crea un nuevo periodo de inventario en la base de datos (por ejemplo, el inventario mensual del negocio).
     * Además, cambia de una vez el estado del negocio a 'activo'.
     */
    public int iniciarInventario(int idNegocio, String tipoControl, Date fechaInicio) {
        Connection con = null; // Conexión apuntada a vacío
        PreparedStatement psInventario = null; // Consulta para la tabla Inventario 
        PreparedStatement psNegocio = null; // Consulta para la tabla Negocio
        ResultSet rsKeys = null; // Para guardar el ID nuevo que se genere
        int idGenerado = -1; // -1 significa que no se guardó todavía
        
        try {
            con = Conexion.getConexion(); // Nos conectamos
            con.setAutoCommit(false); // Ponemos esto en falso para que si falla un paso, no guarde nada a medias (Transacción)
            
            // Comando para insertar el nuevo registro del inventario con sus campos
            String sqlInventario = "INSERT INTO INVENTARIO (id_negocio, tipo_control, estado, fecha_inicio) " +
                                   "VALUES (?, ?, ?, ?)";
            // Le indicamos RETURN_GENERATED_KEYS porque vamos a necesitar saber qué ID de inventario nos dio la BD
            psInventario = con.prepareStatement(sqlInventario, PreparedStatement.RETURN_GENERATED_KEYS);
            psInventario.setInt(1, idNegocio); // Seleccionamos a qué negocio pertenece
            psInventario.setString(2, tipoControl != null ? tipoControl : "mensual");  // Qué tipo es, si viene vacío pone "mensual"
            psInventario.setString(3, "activo"); // El inventario arranca marcado como activo obligatoriamente
            psInventario.setDate(4, fechaInicio); // Guardamos la fecha recibida
            
            int filas = psInventario.executeUpdate(); // Ejecutamos la inserción
            
            if (filas > 0) { // Si sí se creó el registro del inventario...
                rsKeys = psInventario.getGeneratedKeys(); // Solicitamos su nuevo ID
                if (rsKeys.next()) {
                    idGenerado = rsKeys.getInt(1); // Leemos el id y lo guardamos
                }
                
                // Ahora actualizamos la tabla NEGOCIO y le ponemos estado 'activo' a su dueño
                String sqlNegocio = "UPDATE NEGOCIO SET estado = 'activo' WHERE id_negocio = ?";
                psNegocio = con.prepareStatement(sqlNegocio); // Preparamos
                psNegocio.setInt(1, idNegocio); // Le inyectamos aquí el ID del negocio afectado
                psNegocio.executeUpdate(); // Ejecutamos la actualización
                
                con.commit(); // Si ambos pasos fueron un éxito, confirmamos que LA TRANSACCION VALE
                System.out.println("DAO: Inventario iniciado con ID: " + idGenerado + " para Negocio: " + idNegocio);
            } else {
                con.rollback(); // Si el inventario no pudo insertarse, echamos todo para atrás
            }
            
        } catch (SQLException e) {
            System.err.println("Error al iniciar inventario: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // Echar atrás si hay error grave SQL
            }
        } finally {
            try {
                if (con != null) con.setAutoCommit(true); // Dejar la configuración del servidor web como estaba
                if (rsKeys != null) rsKeys.close();
                if (psNegocio != null) psNegocio.close();
                if (psInventario != null) psInventario.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return idGenerado; // Retornamos el id asignado, o -1 si hubo error
    }
    
    /**
     * Busca si el negocio tiene un inventario actualmente abierto.
     * Solo retorna aquel inventario que de la base de datos tenga estado "activo".
     */
    public Inventario obtenerInventarioActivo(int idNegocio) {
        Inventario inv = null; // Empezamos en null porque puede no haber ninguno abierto
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion(); // Nos conectamos   
            // Busca toda la filla de INVENTARIO pero exige que el estado sea obligatoriamente 'activo'
            String sql = "SELECT * FROM INVENTARIO WHERE id_negocio = ? AND estado = 'activo'";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio); // Insertamos ID
            rs = ps.executeQuery(); // Mandamos búsqueda
            
            if (rs.next()) { // Si se encontró algo
                inv = new Inventario(); // Creamos objeto vacío
                inv.setIdInventario(rs.getInt("id_inventario")); // Pasamos el ID del inventario
                inv.setIdNegocio(rs.getInt("id_negocio")); // Pasamos el del negocio
                inv.setFechaInicio(rs.getDate("fecha_inicio")); // Pasamos qué día arrancó
                inv.setTipoControl(rs.getString("tipo_control")); // Semanal, mensual, etc
                inv.setEstado(rs.getString("estado")); // El estado
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener inventario activo: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return inv; // Devolvemos el inventario (si existe) o nulo si no existe
    }

    /**
     * Trae absolutamente todos los inventarios que ha tenido el negocio (tanto activos como ya cerrados).
     * Ideal para hacer el historial general de cortes de mes.
     */
    public java.util.List<Inventario> listarInventariosPorNegocio(int idNegocio) {
        java.util.List<Inventario> lista = new java.util.ArrayList<>(); // Creamos la estructura lista para llenar
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // Trae todo de INVENTARIO para ese negocio, ordenándolo por fecha desde la más reciente hacia atrás  
            String sql = "SELECT * FROM INVENTARIO WHERE id_negocio = ? ORDER BY fecha_inicio DESC";
            ps = con.prepareStatement(sql); // Armamos la consulta
            ps.setInt(1, idNegocio); // Filtramos por el id del negocio
            rs = ps.executeQuery(); // Pedimos al SQL que busque
            
            while (rs.next()) { // Por cada registro encontrado en el historial:
                Inventario inv = new Inventario(); // Nuevo objeto para guardar sus datos
                inv.setIdInventario(rs.getInt("id_inventario")); // Traemos ID
                inv.setIdNegocio(rs.getInt("id_negocio")); // Traemos el dueño
                inv.setFechaInicio(rs.getDate("fecha_inicio")); // Traemos fecha de creación
                inv.setTipoControl(rs.getString("tipo_control")); // Regla de tiempo
                inv.setEstado(rs.getString("estado")); // ¿Activo o Inactivo?
                lista.add(inv); // Lo mandamos a la lista
            }
        } catch (SQLException e) {
            System.err.println("Error al listar inventarios: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close(); // Liberamos cursores
                if (ps != null) ps.close(); // Liberamos comandos
                if (con != null) con.close(); // Liberamos enlace
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; // Se envía la colección recabada
    }

    /**
     * Actualiza el inventario en la base de datos marcándolo como 'inactivo'.
     * Significa que se cerró el mes/periodo y ya no se pueden agregar más facturas ahí.
     */
    public boolean finalizarInventario(int idInventario) {
        Connection con = null;
        PreparedStatement psEnv = null;
        boolean finalizado = false; // Nos sirve para saber si sí se hizo el UPDATE a tiempo
        
        try {
            con = Conexion.getConexion(); 
            // Modifica (UPDATE) la tabla INVENTARIO exclusivamente su columna estado a 'inactivo'
            String sqlInv = "UPDATE INVENTARIO SET estado = 'inactivo' WHERE id_inventario = ?";
            psEnv = con.prepareStatement(sqlInv); // Preparamos sql
            psEnv.setInt(1, idInventario); // Le indicamos a a la base cual ID tiene que cambiar
            int f1 = psEnv.executeUpdate(); // Ejecuta 
            
            if (f1 > 0) { // Si alteró una fila correctamente en la bd
                finalizado = true; // Todo bien
            }
            
        } catch (SQLException e) {
            System.err.println("Error al finalizar inventario: " + e.getMessage());
        } finally {
            try {
                if (psEnv != null) psEnv.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return finalizado; // Devuelve la confirmación boolean
    }

    /**
     * Sirve para buscar específicamente cuál fue el ÚLTIMO inventario que cerraron.
     * Útil por ejemplo para comparar el mes pasado contra este mes.
     */
    public Inventario obtenerUltimoInventarioCerrado(int idNegocio) {
        Inventario inv = null; // Iniciamos con que "nada fue encontrado"
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // Acá solicitamos a INVENTARIO una fila con estado inactivo 
            // La base de datos es ordenada con ORDER ... DESC y se le pone LIMIT 1 para coger solo el primero que aparezca de arriba hacia abajo (el último cerrado de verdad)
            String sql = "SELECT * FROM INVENTARIO WHERE id_negocio = ? AND estado = 'inactivo' ORDER BY id_inventario DESC LIMIT 1";
            ps = con.prepareStatement(sql); // Lo preparamos
            ps.setInt(1, idNegocio); // Le damos el id negocio
            rs = ps.executeQuery(); // Efectuamos la petición
            
            if (rs.next()) { // Si en efecto hubo un último inventario (porque de pronto es un negocio tan nuevo que este es su primer mes y no hay cerrados)
                inv = new Inventario(); // Lo fabricamos en Java
                inv.setIdInventario(rs.getInt("id_inventario")); // Le metemos su ID
                inv.setIdNegocio(rs.getInt("id_negocio")); // Su negocio
                inv.setFechaInicio(rs.getDate("fecha_inicio")); // Inicio de mes
                inv.setTipoControl(rs.getString("tipo_control"));
                inv.setEstado(rs.getString("estado"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener último inventario cerrado: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return inv; // Y se lo servimos al controlador que lo pidio
    }
}
