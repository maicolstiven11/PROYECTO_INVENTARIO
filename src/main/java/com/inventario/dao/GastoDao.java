package com.inventario.dao;

// =====================================================================
// IMPORTACIONES NECESARIAS
// =====================================================================
import com.inventario.model.Gasto;    // Modelo POJO que representa la tabla GASTO_DIARIO (ver: model/Gasto.java)
import com.inventario.util.Conexion;  // Clase utilitaria para conectar a MySQL (ver: util/Conexion.java)
import java.sql.Connection;           // Conexión abierta con la base de datos
import java.sql.PreparedStatement;    // Consulta SQL segura con parámetros (?)
import java.sql.ResultSet;           // Resultados de un SELECT
import java.sql.SQLException;        // Errores de SQL

/**
 * DAO: Clase GastoDao (Data Access Object)
 * 
 * Esta clase maneja TODAS las operaciones de la tabla GASTO_DIARIO en MySQL.
 * Un gasto diario es un egreso registrado por el trabajador dentro de un inventario activo.
 * 
 * QUIÉN LA USA:
 * - GastoServlet.java: Para registrar gastos (doPost) y listar gastos (doGet, action=listar)
 * 
 * TABLA QUE MANEJA: GASTO_DIARIO
 * Columnas: id_gastos, id_inventario, cantidad, fecha, subtotal, descripcion
 */
public class GastoDao {

    /**
     * 1. REGISTRAR UN NUEVO GASTO
     * 
     * QUIÉN LO LLAMA: GastoServlet.doPost() → Cuando el usuario envía agregar_gasto.html
     * QUÉ RECIBE: Objeto Gasto con los datos del formulario:
     *   - id_inventario: Viene de session.getAttribute("idInventarioActual") en GastoServlet
     *   - cantidad: Viene de agregar_gasto.html → input name="cantidad"
     *   - fecha: Viene de agregar_gasto.html → input name="fecha" (type="date")
     *   - subtotal: Viene de agregar_gasto.html → input name="subtotal"
     *   - descripcion: Viene de agregar_gasto.html → textarea name="descripcion"
     * QUÉ RETORNA: true si se insertó correctamente, false si falló
     * QUÉ HACE EN LA BD: INSERT INTO GASTO_DIARIO (...) VALUES (?, ?, ?, ?, ?)
     */
    public boolean registrarGasto(Gasto g) throws SQLException{
        Connection con = null;
        PreparedStatement ps = null;
        boolean registrado = false;
        
        try{
            con = Conexion.getConexion(); // Abrimos conexión a MySQL
            // SQL con 5 parámetros: id_inventario, cantidad, fecha, subtotal, descripcion
            String sql = "INSERT INTO GASTO_DIARIO (id_inventario, cantidad, fecha, subtotal, descripcion) VALUES (?,?,?,?,?)";
            
            ps = con.prepareStatement(sql);
            
            ps.setInt(1, g.getId_inventario());   // ? #1 ← Modelo: g.getId_inventario() ← Servlet: session.getAttribute("idInventarioActual")
            ps.setInt(2, g.getCantidad());         // ? #2 ← Modelo: g.getCantidad() ← Servlet: Integer.parseInt(request.getParameter("cantidad"))
            ps.setDate(3, g.getFecha());           // ? #3 ← Modelo: g.getFecha() ← Servlet: Date.valueOf(request.getParameter("fecha"))
            ps.setDouble(4, g.getSubtotal());      // ? #4 ← Modelo: g.getSubtotal() ← Servlet: Double.parseDouble(request.getParameter("subtotal"))
            ps.setString(5, g.getDescripcion());   // ? #5 ← Modelo: g.getDescripcion() ← Servlet: request.getParameter("descripcion")
            
            if(ps.executeUpdate()>0){ // Si se insertó al menos 1 fila, fue exitoso
                registrado = true;
            }
        }catch (SQLException e){
            e.printStackTrace(); // Mostramos el error en consola del servidor
        }finally {
            // Cerrar recursos para liberar la conexión a la BD
            try {
                if (ps != null) ps.close(); 
                if (con != null) con.close(); 
            } catch (SQLException ex) {
                ex.printStackTrace(); 
            }
        }
    
        return registrado; // true = registrado, false = falló
    }

    /**
     * 2. LISTAR GASTOS POR NEGOCIO
     * 
     * QUIÉN LO LLAMA: GastoServlet.doGet(action=listar) → Para mostrar historial de gastos
     * QUÉ RECIBE: int idNegocio → ID del negocio actual (viene de: session.getAttribute("idNegocioActual"))
     * QUÉ RETORNA: Lista de objetos Gasto ordenados por fecha descendente
     * QUÉ HACE EN LA BD:
     *   - SELECT de GASTO_DIARIO
     *   - JOIN con INVENTARIO porque GASTO_DIARIO tiene FK a INVENTARIO (id_inventario),
     *     y necesitamos filtrar por negocio (que está en INVENTARIO, no en GASTO_DIARIO)
     * 
     * DESTINO FINAL: request.setAttribute("listaGastos", lista) → visualizar_gastos.jsp
     *   En el JSP se accede como: ${gasto.descripcion}, ${gasto.subtotal}, ${gasto.fecha}
     */
    public java.util.List<Gasto> listarGastos(int idNegocio) {
        java.util.List<Gasto> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // JOIN con INVENTARIO necesario para filtrar gastos por negocio
            // La cadena es: GASTO_DIARIO → INVENTARIO → NEGOCIO
            String sql = "SELECT g.* FROM GASTO_DIARIO g " +
                         "INNER JOIN INVENTARIO i ON g.id_inventario = i.id_inventario " + // Gasto → Inventario
                         "WHERE i.id_negocio = ? " +                                       // Inventario → Negocio específico
                         "ORDER BY g.fecha DESC";                                           // Más recientes primero
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio); // ? ← ID del negocio actual (de la sesión del Servlet)
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Gasto g = new Gasto();
                g.setId_gastos(rs.getInt("id_gastos"));         // Columna BD → Modelo. Usado en JSP: ${gasto.id_gastos}
                g.setId_inventario(rs.getInt("id_inventario")); // Columna BD → Modelo. Para referencia interna
                g.setCantidad(rs.getInt("cantidad"));           // Columna BD → Modelo. Usado en JSP: ${gasto.cantidad}
                g.setFecha(rs.getDate("fecha"));               // Columna BD → Modelo. Usado en JSP: ${gasto.fecha}
                g.setSubtotal(rs.getDouble("subtotal"));       // Columna BD → Modelo. Usado en JSP: ${gasto.subtotal}
                g.setDescripcion(rs.getString("descripcion")); // Columna BD → Modelo. Usado en JSP: ${gasto.descripcion}
                
                lista.add(g);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Gastos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; // Retorna al GastoServlet → se pone en request → llega a visualizar_gastos.jsp
    }
}
