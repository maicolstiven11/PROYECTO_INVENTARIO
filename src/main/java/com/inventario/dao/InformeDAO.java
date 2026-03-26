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
        Connection con = null; // Variable para gestionar el enchufe a MySQL
        PreparedStatement ps = null; // Variable para preparar la pregunta a la base de datos
        ResultSet rs = null; // Variable para recibir la respuesta (el número sumado)
        
        try {
            con = Conexion.getConexion(); // Nos conectamos
            
            // CONSULTA SQL (Cálculo Matemático):
            // 1. SELECT COALESCE(SUM(total_venta), 0): Suma toda la columna 'total_venta'.
            //    COALESCE sirve para que si no hay ventas devuelva un 0 en vez de un valor vacío (null).
            // 2. AS total: Le da un "apodo" o alias al resultado para poder leerlo fácilmente en Java.
            // 3. FROM VENTA: Indica que sumaremos los registros de la tabla de ventas.
            // 4. WHERE id_inventario = ?: Filtra únicamente las ventas del mes/periodo solicitado.
            String sql = "SELECT COALESCE(SUM(total_venta), 0) AS total FROM VENTA WHERE id_inventario = ?";
            
            ps = con.prepareStatement(sql); // Enviamos el encargo al servidor de BD
            ps.setInt(1, idInventario); // Reemplazamos el '?' con el código del inventario actual
            rs = ps.executeQuery(); // Activamos la suma en el servidor
            
            if (rs.next()) { // Si logramos obtener un resultado del cálculo
                total = rs.getDouble("total"); // Extraemos el número decimal usando el alias 'total'
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total ventas: " + e.getMessage()); // Reporte de fallo técnico
        } finally {
            // Limpieza reglamentaria de recursos para que el servidor no se ponga lento
            try {
                if (rs != null) rs.close(); 
                if (ps != null) ps.close(); 
                if (con != null) con.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return total; // Retornamos el dinero total sumado para el reporte
    }

    /**
     * Calcula cuánto dinero en total se ha gastado (viáticos, servicios, etc.) 
     * en este inventario.
     */
    public double obtenerTotalGastos(int idInventario) {
        double total = 0; // Total arranca en cero
        Connection con = null; // Enlace a BD
        PreparedStatement ps = null; // Preparador SQL
        ResultSet rs = null; // Receptor de la suma
        
        try {
            con = Conexion.getConexion(); // Abrimos conexión
            
            // CONSULTA SQL (Suma de Gastos):
            // 1. SELECT COALESCE(SUM(subtotal), 0): Suma todos los 'subtotal' de la tabla de gastos.
            //    Si la tabla está limpia, nos devuelve 0 gracias al COALESCE.
            // 2. AS total: Alias para encontrar el resultado en el ResultSet.
            // 3. FROM GASTO_DIARIO: Tabla donde se anotan los egresos de dinero.
            // 4. WHERE id_inventario = ?: Restringimos la suma solo al inventario que estamos viendo.
            String sql = "SELECT COALESCE(SUM(subtotal), 0) AS total FROM GASTO_DIARIO WHERE id_inventario = ?";
            
            ps = con.prepareStatement(sql); // Cargamos el SQL
            ps.setInt(1, idInventario); // Seteamos el filtro de mes
            rs = ps.executeQuery(); // Disparamos la suma en MySQL
            
            if (rs.next()) { // Si hay respuesta
                total = rs.getDouble("total"); // Recuperamos la cifra de dinero gastado
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total gastos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close(); 
                if (ps != null) ps.close(); 
                if (con != null) con.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return total; // Enviamos el total de gastos calculado
    }

    /**
     * Calcula cuánto dinero en total se ha invertido comprando productos a los proveedores
     * (Pedidos a proveedores) en el periodo del inventario.
     */
    public double obtenerTotalPedidos(int idInventario) {
        double total = 0; // Todo empieza en nada
        Connection con = null; // Variable para el socket de BD
        PreparedStatement ps = null; // Variable para el comando
        ResultSet rs = null; // Variable para el bulto de datos
        
        try {
            con = Conexion.getConexion(); // Conectamos
            
            // CONSULTA SQL (Suma de Compras):
            // 1. SELECT COALESCE(SUM(total_pedido), 0): Suma lo que le hemos pagado a proveedores.
            // 2. FROM PEDIDOS_PROVEEDOR: Tabla de facturas de compras hechas para el bar o negocio.
            // 3. WHERE id_inventario = ?: Queremos saber solo cuánto se invirtió en ESTE periodo.
            String sql = "SELECT COALESCE(SUM(total_pedido), 0) AS total FROM PEDIDOS_PROVEEDOR WHERE id_inventario = ?";
            
            ps = con.prepareStatement(sql); 
            ps.setInt(1, idInventario); // Entregamos el ID de inventario
            rs = ps.executeQuery(); // Disparamos consulta de suma
            
            if (rs.next()) {
                total = rs.getDouble("total"); // Leemos la cifra económica sumada
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
        return total; // Retornamos ese valioso dato contable
    }

    /**
     * Cuenta la cantidad de facturas de venta separadas que se hicieron en este inventario.
     * No suma dinero, solo cuenta las facturas.
     */
    public int obtenerCantidadVentas(int idInventario) {
        int cantidad = 0; // Arranca en cero el contador de tickets
        Connection con = null; // Objeto conector
        PreparedStatement ps = null; // Objeto consultor
        ResultSet rs = null; // Objeto receptor
        
        try {
            con = Conexion.getConexion(); // Conexión activa
            
            // CONSULTA SQL (Conteo de Filas):
            // 1. SELECT COUNT(*): En lugar de sumar dinero, cuenta cuántos renglones o facturas existen.
            // 2. AS total: Alias para leer el número del contador.
            // 3. FROM VENTA: Tabla de tickets de venta.
            // 4. WHERE id_inventario = ?: Filtro para no contar facturas de otros meses.
            String sql = "SELECT COUNT(*) AS total FROM VENTA WHERE id_inventario = ?";
            
            ps = con.prepareStatement(sql); // Armamos la pregunta a SQL
            ps.setInt(1, idInventario); // Le damos el ID del periodo actual
            rs = ps.executeQuery(); // SQL nos responde con el número contado
            
            if (rs.next()) {
                cantidad = rs.getInt("total"); // Asignamos lo que contó la BD al contador de Java
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
        return cantidad; // Devolvemos el conteo final de facturas emitidas
    }
}
