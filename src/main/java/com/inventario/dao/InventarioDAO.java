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
    /**
     * Crea un nuevo periodo de inventario en la base de datos (por ejemplo, el inventario mensual del negocio).
     * Además, cambia de una vez el estado del negocio a 'activo'.
     */
    public int iniciarInventario(int idNegocio, String tipoControl, Date fechaInicio) {
        Connection con = null; // Variable para gestionar el enchufe a MySQL
        PreparedStatement psInventario = null; // Consulta para la tabla Inventario 
        PreparedStatement psNegocio = null; // Consulta para la tabla Negocio
        ResultSet rsKeys = null; // Para guardar el ID nuevo que se genere
        int idGenerado = -1; // -1 significa que no se guardó todavía
        
        try {
            con = Conexion.getConexion(); // Nos conectamos
            
            // TRANSACCIÓN: Desactivamos el auto-guardado (AutoCommit) para que ambos pasos
            // se guarden juntos. Si uno falla, el otro NO se guarda.
            con.setAutoCommit(false); 
            
            // CONSULTA SQL 1 (Inserción de Inventario):
            // 1. INSERT INTO INVENTARIO: Tabla donde se crean los periodos.
            // 2. VALUES (?, ?, ?, ?): Datos de negocio, tipo, estado ('activo') y fecha inicial.
            String sqlInventario = "INSERT INTO INVENTARIO (id_negocio, tipo_control, estado, fecha_inicio) " +
                                   "VALUES (?, ?, ?, ?)";
            
            // Usamos RETURN_GENERATED_KEYS para capturar el ID que MySQL le asigne automáticamente
            psInventario = con.prepareStatement(sqlInventario, PreparedStatement.RETURN_GENERATED_KEYS);
            psInventario.setInt(1, idNegocio); // Quién es el dueño
            psInventario.setString(2, tipoControl != null ? tipoControl : "mensual");  // Control de tiempo
            psInventario.setString(3, "activo"); // Estado inicial
            psInventario.setDate(4, fechaInicio); // Fecha de arranque
            
            int filas = psInventario.executeUpdate(); // Ejecutamos la inserción
            
            if (filas > 0) { // Si el inventario se creó con éxito...
                rsKeys = psInventario.getGeneratedKeys(); // Pedimos el ID recién nacido
                if (rsKeys.next()) {
                    idGenerado = rsKeys.getInt(1); // Lo guardamos en nuestra variable de Java
                }
                
                // CONSULTA SQL 2 (Actualización de Negocio):
                // 1. UPDATE NEGOCIO SET estado = 'activo': Marcamos el bar/negocio como operativo.
                // 2. WHERE id_negocio = ?: Solo afectamos al dueño de este inventario.
                String sqlNegocio = "UPDATE NEGOCIO SET estado = 'activo' WHERE id_negocio = ?";
                psNegocio = con.prepareStatement(sqlNegocio); 
                psNegocio.setInt(1, idNegocio); 
                psNegocio.executeUpdate(); 
                
                // FINALIZACIÓN EXITOSA: Guardamos físicamente los dos cambios en el disco duro
                con.commit(); 
                System.out.println("DAO: Inventario iniciado con ID: " + idGenerado + " para Negocio: " + idNegocio);
            } else {
                // FALLO: Si no se pudo crear el inventario, deshacemos cualquier cambio pendiente
                con.rollback(); 
            }
            
        } catch (SQLException e) {
            System.err.println("Error al iniciar inventario: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // Echar atrás si hay error
            }
        } finally {
            try {
                // Limpieza y restauración de la configuración de conexión
                if (con != null) con.setAutoCommit(true); 
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
        Inventario inv = null; // Empezamos asumiendo que no hay nada abierto
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion(); // Enlace a MySQL
            
            // CONSULTA SQL (Selección con Filtro Doble):
            // 1. SELECT *: Pide todas las columnas (ID, fecha, tipo, etc).
            // 2. WHERE id_negocio = ?: Filtra por el bar del usuario.
            // 3. AND estado = 'activo': Condición vital para no traer inventarios que ya se cerraron.
            String sql = "SELECT * FROM INVENTARIO WHERE id_negocio = ? AND estado = 'activo'";
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio); 
            rs = ps.executeQuery(); // Disparamos la búsqueda
            
            if (rs.next()) { // Si el servidor encontró un inventario abierto
                inv = new Inventario(); // Creamos la "caja" de Java
                // Llenamos la caja con los datos de las columnas de MySQL:
                inv.setIdInventario(rs.getInt("id_inventario")); 
                inv.setIdNegocio(rs.getInt("id_negocio")); 
                inv.setFechaInicio(rs.getDate("fecha_inicio")); 
                inv.setTipoControl(rs.getString("tipo_control")); 
                inv.setEstado(rs.getString("estado")); 
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
        return inv; // Retornamos el objeto lleno o null si no había nada activo
    }

    /**
     * Trae absolutamente todos los inventarios que ha tenido el negocio (tanto activos como ya cerrados).
     * Ideal para hacer el historial general de cortes de mes.
     */
    public java.util.List<Inventario> listarInventariosPorNegocio(int idNegocio) {
        java.util.List<Inventario> lista = new java.util.ArrayList<>(); // Bolsa para guardar el historial
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            
            // CONSULTA SQL (Listado Histórico):
            // 1. SELECT *: Selecciona todos los inventarios.
            // 2. WHERE id_negocio = ?: Del negocio actual.
            // 3. ORDER BY fecha_inicio DESC: Ordenamos para que el más nuevo salga de primero en la tabla.
            String sql = "SELECT * FROM INVENTARIO WHERE id_negocio = ? ORDER BY fecha_inicio DESC";
            
            ps = con.prepareStatement(sql); 
            ps.setInt(1, idNegocio); 
            rs = ps.executeQuery(); 
            
            while (rs.next()) { // Recorremos fila por fila mientras MySQL tenga datos
                Inventario inv = new Inventario(); 
                inv.setIdInventario(rs.getInt("id_inventario")); 
                inv.setIdNegocio(rs.getInt("id_negocio")); 
                inv.setFechaInicio(rs.getDate("fecha_inicio")); 
                inv.setTipoControl(rs.getString("tipo_control")); 
                inv.setEstado(rs.getString("estado")); 
                lista.add(inv); // Agregamos a la lista para enviarla a la vista Web (JSP)
            }
        } catch (SQLException e) {
            System.err.println("Error al listar inventarios: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close(); 
                if (ps != null) ps.close(); 
                if (con != null) con.close(); 
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
        PreparedStatement ps = null;
        boolean finalizado = false; // Variable de confirmación
        
        try {
            con = Conexion.getConexion(); // Nos conectamos
            
            // CONSULTA SQL (Cierre de Periodo):
            // 1. UPDATE INVENTARIO: Vamos a cambiar un dato existente.
            // 2. SET estado = 'inactivo': Ponemos el candado al inventario.
            // 3. WHERE id_inventario = ?: Únicamente cerramos el que el usuario seleccionó.
            String sql = "UPDATE INVENTARIO SET estado = 'inactivo' WHERE id_inventario = ?";
            
            ps = con.prepareStatement(sql); 
            ps.setInt(1, idInventario); 
            int filasModificadas = ps.executeUpdate(); // Realizamos el cambio físico
            
            if (filasModificadas > 0) { 
                finalizado = true; // Si MySQL confirmó la edición, devolvemos éxito
            }
            
        } catch (SQLException e) {
            System.err.println("Error al finalizar inventario: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return finalizado; // Retornamos true si se pudo cerrar el candado
    }

    /**
     * Sirve para buscar específicamente cuál fue el ÚLTIMO inventario que cerraron.
     * Útil por ejemplo para comparar el mes pasado contra este mes.
     */
    public Inventario obtenerUltimoInventarioCerrado(int idNegocio) {
        Inventario inv = null; // Iniciamos asumiendo que es el primero de la historia
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion(); 
            
            // CONSULTA SQL (Búsqueda del más reciente cerrado):
            // 1. WHERE estado = 'inactivo': Solo nos interesan los que ya terminaron.
            // 2. ORDER BY id_inventario DESC: Los ordena de mayor a menor (el último creado arriba).
            // 3. LIMIT 1: Solo nos traemos la primera fila, que es precisamente el último que se cerró.
            String sql = "SELECT * FROM INVENTARIO WHERE id_negocio = ? AND estado = 'inactivo' ORDER BY id_inventario DESC LIMIT 1";
            
            ps = con.prepareStatement(sql); 
            ps.setInt(1, idNegocio); 
            rs = ps.executeQuery(); 
            
            if (rs.next()) { // Si el servidor encontró el historial previo
                inv = new Inventario(); 
                inv.setIdInventario(rs.getInt("id_inventario")); 
                inv.setIdNegocio(rs.getInt("id_negocio")); 
                inv.setFechaInicio(rs.getDate("fecha_inicio")); 
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
        return inv; // Retornamos el inventario clausurado más reciente
    }
}
