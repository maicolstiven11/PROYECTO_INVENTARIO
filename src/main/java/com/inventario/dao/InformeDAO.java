package com.inventario.dao;

import com.inventario.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase InformeDAO.
 *
 * Esta clase se encarga exclusivamente de calcular los totales matemáticos 
 * (ventas, gastos, pedidos) para mostrarlos en el panel de informes. 
 * Solo hace consultas de lectura (SELECT) usando funciones de suma y conteo.
 */
public class InformeDAO {

    /**
     * Calcula cuánto dinero en total se ha vendido durante un inventario específico.
     * Suma todos los registros de la tabla VENTA.
     */
    public double obtenerTotalVentas(int idInventario) {
        double total = 0; // Inicializamos el total en 0 plata
        Connection con = null; // Declaramos la conexión nula
        PreparedStatement ps = null; // Preparador de consultas
        ResultSet rs = null; // Contenedor de respuestas SQL

        try {
            con = Conexion.getConexion(); // Comenzamos conexión
            // Suma (SUM) la columna total_venta de la tabla VENTA para este id_inventario. 
            // COALESCE evita que falle si no hay ventas enviando un 0 en lugar de un campo vacío (null).
            String sql = "SELECT COALESCE(SUM(total_venta), 0) AS total FROM VENTA WHERE id_inventario = ?";
            ps = con.prepareStatement(sql); // Armamos la instrucción
            ps.setInt(1, idInventario); // Le pasamos el número de inventario
            rs = ps.executeQuery(); // La ejecutamos
            if (rs.next()) { // Si logramos obtener un resultado de la suma
                total = rs.getDouble("total"); // Lo convertimos a número decimal y lo asignamos al total
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total ventas: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close(); // Cerrar resultados
                if (ps != null) ps.close(); // Cerrar consulta
                if (con != null) con.close(); // Cerrar conexión
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return total; // Retornamos el dinero sumado
    }

    /**
     * Calcula cuánto dinero en total se ha gastado (viáticos, servicios, etc.) 
     * en este inventario.
     */
    public double obtenerTotalGastos(int idInventario) {
        double total = 0; // Total arranca en cero
        Connection con = null; // Variable DB
        PreparedStatement ps = null; // Variable Consulta
        ResultSet rs = null; // Variable Respuesta

        try {
            con = Conexion.getConexion(); // Nos conectamos
            // Igualmente suma la columna 'subtotal' pero ahora de la tabla GASTO_DIARIO.
            String sql = "SELECT COALESCE(SUM(subtotal), 0) AS total FROM GASTO_DIARIO WHERE id_inventario = ?";
            ps = con.prepareStatement(sql); // Prepara sql
            ps.setInt(1, idInventario); // Pone el ID del inventario
            rs = ps.executeQuery(); // Ejecuta cálculo
            if (rs.next()) { // Si da resultado
                total = rs.getDouble("total"); // Guarda el número de dinero gastado
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total gastos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close(); // Limpia memoria
                if (ps != null) ps.close(); // Limpia consulta
                if (con != null) con.close(); // Limpia conector
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return total; // Devuelve el número final
    }

    /**
     * Calcula cuánto dinero en total se ha invertido comprando productos a los proveedores
     * (Pedidos a proveedores) en el periodo del inventario.
     */
    public double obtenerTotalPedidos(int idInventario) {
        double total = 0; // Todo empieza en nada
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            // Suma la columna total_pedido de la tabla PEDIDOS_PROVEEDOR para el inventario solicitado
            String sql = "SELECT COALESCE(SUM(total_pedido), 0) AS total FROM PEDIDOS_PROVEEDOR WHERE id_inventario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idInventario); // Entregamos el ID
            rs = ps.executeQuery(); // Disparamos consulta
            if (rs.next()) {
                total = rs.getDouble("total"); // Leemos la cifra sumada
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total pedidos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return total; // Retornamos ese valioso dato
    }

    /**
     * Cuenta la cantidad de facturas de venta separadas que se hicieron en este inventario.
     * No suma dinero, solo cuenta las facturas.
     */
    public int obtenerCantidadVentas(int idInventario) {
        int cantidad = 0; // Arranca en cero el contador
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            // COUNT(*) simplemente cuenta el número de filas en la tabla VENTA de ese inventario
            String sql = "SELECT COUNT(*) AS total FROM VENTA WHERE id_inventario = ?";
            ps = con.prepareStatement(sql); // Armamos la pregunta a SQL
            ps.setInt(1, idInventario); // Le damos el ID
            rs = ps.executeQuery(); // SQL nos responde
            if (rs.next()) {
                cantidad = rs.getInt("total"); // Asignamos lo que contó la BD
            }
        } catch (SQLException e) {
            System.err.println("Error al contar ventas: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return cantidad; // Devolvemos el conteo
    }
}
