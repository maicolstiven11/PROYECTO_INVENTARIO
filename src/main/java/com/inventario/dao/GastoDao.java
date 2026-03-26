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
    public boolean registrarGasto(Gasto g) throws SQLException {
        Connection con = null; // Variable para gestionar el enchufe físico a MySQL
        PreparedStatement ps = null; // Variable para preparar la sentencia SQL protegida
        boolean registrado = false; // Bandera de control para confirmar el éxito de la operación

        try {
            con = Conexion.getConexion(); // Obtenemos la conexión desde nuestra clase de utilidad

            // CONSULTA SQL (Inserción):
            // 1. INSERT INTO GASTO_DIARIO: Indicamos la tabla de destino de los datos.
            // 2. (id_inventario, cantidad, fecha, subtotal, descripcion): Columnas donde
            // guardaremos la información.
            // 3. VALUES (?,?,?,?,?): Marcadores de posición para inyectar los datos del
            // objeto Gasto de forma segura.
            String sql = "INSERT INTO GASTO_DIARIO (id_inventario, cantidad, fecha, subtotal, descripcion) VALUES (?,?,?,?,?)";

            ps = con.prepareStatement(sql); // Enviamos el boceto de la consulta al servidor de BD

            // Inyectamos los valores del objeto Java en los campos correspondientes de la
            // tabla:
            ps.setInt(1, g.getId_inventario()); // Código del inventario (mes) al que pertenece el gasto
            ps.setInt(2, g.getCantidad()); // Cuántas unidades se compraron o gastaron
            ps.setDate(3, g.getFecha()); // Fecha exacta del movimiento contable
            ps.setDouble(4, g.getSubtotal()); // El valor total en dinero del gasto
            ps.setString(5, g.getDescripcion()); // El texto descriptivo (Ej: "Pago de luz", "Reparación silla")

            if (ps.executeUpdate() > 0) { // Ejecutamos el cambio. Si MySQL nos dice que afectó una fila, es un éxito.
                registrado = true; // Cambiamos la bandera a verdadero
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprime el error técnico en la consola del servidor si algo falla en MySQL
        } finally {
            // BLOQUE DE CIERRE: Es obligatorio soltar los recursos para no saturar la
            // memoria del servidor
            try {
                if (ps != null)
                    ps.close(); // Cerramos el preparador de la orden SQL
                if (con != null)
                    con.close(); // Cerramos la conexión a la base de datos
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return registrado; // Retornamos si se pudo guardar el gasto o no
    }

    /**
     * Lista todos los gastos registrados que pertenecen a un negocio en particular.
     * Para saber de qué negocio es el gasto, busca a través de la tabla INVENTARIO.
     */
    public java.util.List<Gasto> listarGastos(int idNegocio) {
        java.util.List<Gasto> lista = new java.util.ArrayList<>(); // Bolsa donde guardaremos todos los gastos
                                                                   // encontrados
        Connection con = null; // Enlace a BD
        PreparedStatement ps = null; // Consultor SQL
        ResultSet rs = null; // Receptor de las filas resultantes de MySQL

        try {
            con = Conexion.getConexion(); // Nos conectamos

            // CONSULTA SQL (Selección con Cruce/JOIN):
            // 1. SELECT g.*: Selecciona absolutamente todas las columnas de la tabla
            // GASTO_DIARIO (alias 'g').
            // 2. FROM GASTO_DIARIO g: Tabla raíz de donde nacen los datos de gastos.
            // 3. INNER JOIN INVENTARIO i ON g.id_inventario = i.id_inventario: Une los
            // gastos con sus inventarios.
            // Hacemos esto porque el gasto NO sabe de quién es el negocio, pero el
            // inventario (alias 'i') SÍ lo sabe.
            // 4. WHERE i.id_negocio = ?: Filtramos para que solo traiga los gastos del
            // administrador/bar actual.
            // 5. ORDER BY g.fecha DESC: Los ordena por fecha, poniendo los más nuevos
            // arriba de la lista.
            String sql = "SELECT g.* FROM GASTO_DIARIO g " +
                    "INNER JOIN INVENTARIO i ON g.id_inventario = i.id_inventario " +
                    "WHERE i.id_negocio = ? " +
                    "ORDER BY g.fecha DESC";

            ps = con.prepareStatement(sql); // Preparamos la búsqueda
            ps.setInt(1, idNegocio); // Le enchufamos el ID del negocio que el usuario tiene abierto
            rs = ps.executeQuery(); // Activamos el radar de búsqueda en MySQL

            while (rs.next()) { // Recorremos cada fila que el radar encontró
                Gasto g = new Gasto(); // Creamos una cajita (objeto) nueva para este gasto específico
                // Extraemos los datos de las columnas y los metemos en la cajita de Java:
                g.setId_gastos(rs.getInt("id_gastos")); // Su ID único
                g.setId_inventario(rs.getInt("id_inventario")); // El mes/id_inventario
                g.setCantidad(rs.getInt("cantidad")); // El volumen
                g.setFecha(rs.getDate("fecha")); // El día que pasó
                g.setSubtotal(rs.getDouble("subtotal")); // El costo
                g.setDescripcion(rs.getString("descripcion")); // El detalle de qué se compró

                lista.add(g); // Insertamos la cajita llena en nuestra lista general
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Gastos: " + e.getMessage()); // Reporte de fallo
        } finally {
            // Limpieza reglamentaria de recursos
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
        return lista; // Se retorna la lista con todos los gastos del negocio listos para mostrarse en
                      // la web
    }
}
