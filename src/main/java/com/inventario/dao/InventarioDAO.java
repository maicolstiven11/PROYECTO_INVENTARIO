package com.inventario.dao;

// =====================================================================
// IMPORTACIONES NECESARIAS
// =====================================================================
import com.inventario.util.Conexion;      // Clase utilitaria para conectar a MySQL (ver: util/Conexion.java)
import com.inventario.model.Inventario;   // Modelo POJO que representa la tabla INVENTARIO (ver: model/Inventario.java)
import java.sql.Connection;               // Conexión abierta con la base de datos
import java.sql.PreparedStatement;        // Consulta SQL segura
import java.sql.ResultSet;               // Resultados de un SELECT
import java.sql.SQLException;            // Errores de SQL
import java.sql.Date;                    // Para manejar fechas

/**
 * DAO: Clase InventarioDAO (Data Access Object)
 * 
 * Esta clase maneja TODAS las operaciones de la tabla INVENTARIO en MySQL.
 * Un Inventario es un periodo de control contable de un bar. Solo puede haber UNO activo por bar.
 * 
 * QUIÉN LA USA:
 * - InventarioServlet.java: Para iniciar inventarios (action=iniciar), entrar al inventario (action=entrar),
 *   finalizar inventario (action=cerrar)
 * - LoginServlet.java: Para obtener el inventario activo del trabajador al iniciar sesión
 * 
 * TABLA QUE MANEJA: INVENTARIO
 * Columnas: id_inventario, id_negocio, tipo_control, estado, fecha_inicio
 * También MODIFICA: NEGOCIO (cambia estado a 'activo' al iniciar inventario)
 */
public class InventarioDAO {

    /**
     * 1. INICIAR UN NUEVO INVENTARIO PARA UN NEGOCIO
     * 
     * QUIÉN LO LLAMA: InventarioServlet.processRequest(action=iniciar)
     *   → Cuando el admin envía el formulario de Inicio_inv.html
     * QUÉ RECIBE:
     *   - int idNegocio: ID del bar. Viene de: Inicio_inv.html → input hidden name="idNegocio" (puesto por la URL)
     *   - String tipoControl: 'semanal' o 'mensual'. Viene de: Inicio_inv.html → select name="tipo"
     *   - Date fechaInicio: Fecha de apertura. Viene de: Inicio_inv.html → input name="fecha" (type="date")
     * QUÉ RETORNA: int → ID del inventario generado, o -1 si falló
     * 
     * TRANSACCIÓN ATÓMICA (2 operaciones):
     * 1. INSERT en INVENTARIO con estado='activo'
     * 2. UPDATE en NEGOCIO para cambiar estado a 'activo'
     */
    public int iniciarInventario(int idNegocio, String tipoControl, Date fechaInicio) {
        Connection con = null;
        PreparedStatement psInventario = null;  // Para insertar en INVENTARIO
        PreparedStatement psNegocio = null;     // Para actualizar estado en NEGOCIO
        ResultSet rsKeys = null;                // Para obtener el ID auto-generado
        int idGenerado = -1;                    // -1 significa que falló
        
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // TRANSACCIÓN: Ambas operaciones deben funcionar o ninguna
            
            // PASO 1: Insertar el nuevo inventario en la tabla INVENTARIO
            String sqlInventario = "INSERT INTO INVENTARIO (id_negocio, tipo_control, estado, fecha_inicio) " +
                                   "VALUES (?, ?, ?, ?)";
            psInventario = con.prepareStatement(sqlInventario, PreparedStatement.RETURN_GENERATED_KEYS);
            psInventario.setInt(1, idNegocio);                                         // ? #1 ← ID del bar
            psInventario.setString(2, tipoControl != null ? tipoControl : "mensual"); // ? #2 ← Tipo de control ('semanal'/'mensual')
            psInventario.setString(3, "activo");                                       // ? #3 ← Siempre empieza como 'activo'
            psInventario.setDate(4, fechaInicio);                                      // ? #4 ← Fecha de inicio del inventario
            
            int filas = psInventario.executeUpdate(); // Ejecuta el INSERT
            if (filas > 0) {
                rsKeys = psInventario.getGeneratedKeys(); // Obtenemos el ID generado por AUTO_INCREMENT
                if (rsKeys.next()) {
                    idGenerado = rsKeys.getInt(1); // El primer campo es el id_inventario generado
                }
                
                // PASO 2: Cambiar el estado del NEGOCIO a 'activo'
                // Esto indica que el bar tiene un inventario en curso
                String sqlNegocio = "UPDATE NEGOCIO SET estado = 'activo' WHERE id_negocio = ?";
                psNegocio = con.prepareStatement(sqlNegocio);
                psNegocio.setInt(1, idNegocio); // ? ← El mismo negocio
                psNegocio.executeUpdate();
                
                con.commit(); // CONFIRMAR ambas operaciones
                System.out.println("DAO: Inventario iniciado con ID: " + idGenerado + " para Negocio: " + idNegocio);
            } else {
                con.rollback(); // Si no se insertó, deshacer
            }
            
        } catch (SQLException e) {
            System.err.println("Error al iniciar inventario: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            // Cerrar TODOS los recursos
            try {
                if (con != null) con.setAutoCommit(true);
                if (rsKeys != null) rsKeys.close();
                if (psNegocio != null) psNegocio.close();
                if (psInventario != null) psInventario.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return idGenerado; // Retorna al InventarioServlet → se guarda en sesión como idInventarioActual
    }
    
    /**
     * 2. OBTENER EL INVENTARIO ACTIVO DE UN NEGOCIO
     * 
     * QUIÉN LO LLAMA:
     *   - InventarioServlet.processRequest(action=entrar) → Para entrar a un inventario existente
     *   - LoginServlet.doPost() → Al iniciar sesión un trabajador, para cargar su inventario activo
     * QUÉ RECIBE: int idNegocio → ID del bar (viene de: URL o sesión)
     * QUÉ RETORNA: Objeto Inventario si tiene uno activo, o null si no tiene
     * QUÉ HACE EN LA BD: SELECT * FROM INVENTARIO WHERE id_negocio = ? AND estado = 'activo'
     * 
     * DESTINO: El Servlet guarda los datos en sesión:
     *   session.setAttribute("idInventarioActual", inv.getIdInventario())
     *   → Esto permite que VentaServlet, GastoServlet y PedidoServlet sepan en qué inventario trabajar
     */
    public Inventario obtenerInventarioActivo(int idNegocio) {
        Inventario inv = null;  // null si no tiene inventario activo
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // Busca UN inventario con estado 'activo' para este negocio
            // Solo debería haber UNO activo a la vez
            String sql = "SELECT * FROM INVENTARIO WHERE id_negocio = ? AND estado = 'activo'";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio); // ? ← ID del negocio
            rs = ps.executeQuery();
            
            if (rs.next()) { // Si encontró un inventario activo
                inv = new Inventario();
                inv.setIdInventario(rs.getInt("id_inventario")); // → se guardará en sesión como idInventarioActual
                inv.setIdNegocio(rs.getInt("id_negocio"));       // → se guardará en sesión como idNegocioActual
                inv.setFechaInicio(rs.getDate("fecha_inicio"));  // → usado en informes y reportes
                inv.setTipoControl(rs.getString("tipo_control"));// → tipo de control del inventario
                inv.setEstado(rs.getString("estado"));           // → siempre será 'activo' aquí
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
        return inv; // Retorna al Servlet → null significa que no hay inventario activo
    }

    /**
     * 3. LISTAR TODOS LOS INVENTARIOS DE UN NEGOCIO (activos + finalizados)
     * 
     * QUIÉN LO LLAMA: InformeServlet → Para mostrar historial de inventarios
     * QUÉ RECIBE: int idNegocio → ID del negocio
     * QUÉ RETORNA: Lista de TODOS los inventarios ordenados por fecha descendente
     * DESTINO: lista_informes.jsp para seleccionar un inventario y ver su informe
     */
    public java.util.List<Inventario> listarInventariosPorNegocio(int idNegocio) {
        java.util.List<Inventario> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // Trae TODOS los inventarios (activos e inactivos) del negocio, ordenados por fecha
            String sql = "SELECT * FROM INVENTARIO WHERE id_negocio = ? ORDER BY fecha_inicio DESC";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio); // ? ← ID del negocio
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Inventario inv = new Inventario();
                inv.setIdInventario(rs.getInt("id_inventario")); // Usado en JSP: ${inv.idInventario}
                inv.setIdNegocio(rs.getInt("id_negocio"));
                inv.setFechaInicio(rs.getDate("fecha_inicio"));  // Usado en JSP: ${inv.fechaInicio}
                inv.setTipoControl(rs.getString("tipo_control"));// Usado en JSP: ${inv.tipoControl}
                inv.setEstado(rs.getString("estado"));           // Usado en JSP: ${inv.estado}
                lista.add(inv);
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
        return lista;
    }

    /**
     * 4. FINALIZAR (CERRAR) UN INVENTARIO ACTIVO
     * 
     * QUIÉN LO LLAMA: InventarioServlet.processRequest(action=cerrar) → Cuando el admin cierra el inventario
     * QUÉ RECIBE: int idInventario → ID del inventario a cerrar (viene de: session.getAttribute("idInventarioActual"))
     * QUÉ RETORNA: true si se cerró correctamente, false si falló
     * QUÉ HACE EN LA BD: UPDATE INVENTARIO SET estado = 'inactivo' WHERE id_inventario = ?
     * 
     * NOTA: El NEGOCIO permanece en estado 'activo' para que se pueda crear un nuevo inventario.
     */
    public boolean finalizarInventario(int idInventario) {
        Connection con = null;
        PreparedStatement psEnv = null;
        boolean finalizado = false;
        
        try {
            con = Conexion.getConexion();
            
            // Solo cambia el estado del inventario a 'inactivo'
            // El negocio sigue 'activo' → se puede crear un nuevo inventario después
            String sqlInv = "UPDATE INVENTARIO SET estado = 'inactivo' WHERE id_inventario = ?";
            psEnv = con.prepareStatement(sqlInv);
            psEnv.setInt(1, idInventario); // ? ← ID del inventario a cerrar
            int f1 = psEnv.executeUpdate();
            
            if (f1 > 0) { // Si se actualizó al menos 1 fila
                finalizado = true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al finalizar inventario: " + e.getMessage());
        } finally {
            try {
                if (psEnv != null) psEnv.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return finalizado; // true = cerrado, false = error
    }
}
