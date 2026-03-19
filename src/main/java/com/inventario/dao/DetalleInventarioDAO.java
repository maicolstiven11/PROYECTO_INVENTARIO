package com.inventario.dao;

import com.inventario.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase DetalleInventarioDAO.
 * 
 * Esta clase se encarga de guardar, buscar y actualizar los detalles de cada producto 
 * en un inventario específico dentro de la base de datos.
 */
public class DetalleInventarioDAO {

    /**
     * Guarda la cantidad inicial de un producto cuando se hace el inventario.
     * Selecciona la tabla INVENTARIO_DETALLE y le pasa el ID del inventario, el ID del producto y cuántos hay en el momento.
     */
    public boolean insertarDetalle(int idInventario, int idProducto, double cantidadInicial) {
        Connection con = null; // Preparamos la conexión como nula al inicio
        PreparedStatement ps = null; // Sirve para armar la consulta SQL
        boolean registrado = false; // Variable para saber si se guardó con éxito
        
        try {
            con = Conexion.getConexion(); // Nos conectamos a la base de datos
            // Esta consulta inserta una nueva fila en la tabla INVENTARIO_DETALLE con el producto y sus datos
            String sql = "INSERT INTO INVENTARIO_DETALLE (id_inventario, id_producto, cantidad_inicial) VALUES (?, ?, ?)";
            ps = con.prepareStatement(sql); // Preparamos la consulta
            ps.setInt(1, idInventario); // Reemplazamos el primer ? con el idInventario
            ps.setInt(2, idProducto); // Reemplazamos el segundo ? con el idProducto
            ps.setDouble(3, cantidadInicial); // Reemplazamos el tercer ? con la cantidad inicial
            
            int filas = ps.executeUpdate(); // Ejecutamos la consulta en la base de datos
            registrado = (filas > 0); // Si afectó más de 0 filas, significa que se guardó bien
            
        } catch (SQLException e) {
            System.err.println("Error al insertar detalle inventario: " + e.getMessage()); // Muestra error si falla algo en la BD
        } finally {
            try {
                if (ps != null) ps.close(); // Cerramos la consulta para liberar memoria
                if (con != null) con.close(); // Cerramos la conexión a la base de datos
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return registrado; // Retornamos verdadero si guardó, falso si falló
    }

    /**
     * Trae todos los detalles de un inventario con el nombre del producto incluido.
     * Une la tabla INVENTARIO_DETALLE con la tabla PRODUCTO para poder sacar el nombre.
     */
    public java.util.List<com.inventario.model.DetalleInventario> listarDetalles(int idInventario) {
        java.util.List<com.inventario.model.DetalleInventario> lista = new java.util.ArrayList<>(); // Creamos una lista vacía para guardar los resultados
        Connection con = null; // Variable para la conexión
        PreparedStatement ps = null; // Para la consulta SQL
        ResultSet rs = null; // Para guardar los resultados que nos devuelve la base de datos
        
        try {
            con = Conexion.getConexion(); // Pedimos conexión a la base de datos
            // Buscamos todo en INVENTARIO_DETALLE y lo combinamos (JOIN) con PRODUCTO usando id_producto para saber cómo se llama
            String sql = "SELECT d.*, p.nombre FROM INVENTARIO_DETALLE d " +
                         "JOIN PRODUCTO p ON d.id_producto = p.id_producto " +
                         "WHERE d.id_inventario = ?"; // Filtramos solo por el inventario que queremos ver
            ps = con.prepareStatement(sql); // Preparamos la consulta
            ps.setInt(1, idInventario); // Inyectamos el ID del inventario en la consulta
            rs = ps.executeQuery(); // Ejecutamos y guardamos la tabla de resultados en rs
            
            while (rs.next()) { // Recorremos fila por fila los resultados que llegaron
                com.inventario.model.DetalleInventario d = new com.inventario.model.DetalleInventario(); // Creamos un nuevo objeto DetalleInventario vacío
                d.setIdDetalle(rs.getInt("id_detalle")); // Le asignamos el ID del detalle que vino de la base de datos
                d.setIdInventario(rs.getInt("id_inventario")); // Asignamos el ID del inventario
                d.setIdProducto(rs.getInt("id_producto")); // Asignamos el ID del producto
                d.setCantidadInicial(rs.getDouble("cantidad_inicial")); // Asignamos la cantidad inicial
                d.setCantidadFinal(rs.getDouble("cantidad_final")); // Asignamos la cantidad final
                d.setNombreProducto(rs.getString("nombre")); // Asignamos el nombre del producto que vino del JOIN
                lista.add(d); // Agregamos este objeto completo a nuestra lista
            }
        } catch (SQLException e) {
            System.err.println("Error al listar detalles inventario: " + e.getMessage()); // Imprime si hay un error
        } finally {
            try {
                if (rs != null) rs.close(); // Cerramos los resultados
                if (ps != null) ps.close(); // Cerramos la consulta
                if (con != null) con.close(); // Cerramos la conexión
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; // Devolvemos la lista llena con los detalles
    }

    /**
     * Actualiza solo la cantidad final de un producto en el inventario.
     * Modifica la tabla INVENTARIO_DETALLE para reemplazar la cantidad_final donde coincidan el inventario y el producto.
     */
    public boolean actualizarCantidadFinal(int idInventario, int idProducto, double cantidadFinal) {
        Connection con = null; // Variable de conexión
        PreparedStatement ps = null; // Variable de la consulta
        boolean actualizado = false; // Bandera de confirmación
        
        try {
            con = Conexion.getConexion(); // Conectamos
            // Actualizamos (UPDATE) la tabla INVENTARIO_DETALLE poniendo la nueva cantidad_final filtrando por ID del inventario y del producto
            String sql = "UPDATE INVENTARIO_DETALLE SET cantidad_final = ? WHERE id_inventario = ? AND id_producto = ?";
            ps = con.prepareStatement(sql); // Preparamos consulta
            ps.setDouble(1, cantidadFinal); // El primer ? es la nueva cantidad
            ps.setInt(2, idInventario); // El segundo ? es el id de inventario
            ps.setInt(3, idProducto); // El tercer ? es el id del producto
            
            int filas = ps.executeUpdate(); // Se ejecuta la acción en base de datos
            actualizado = (filas > 0); // Si modificó mínimo una fila, fue exitoso
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cantidad final: " + e.getMessage()); // Log de error
        } finally {
            try {
                if (ps != null) ps.close(); // Cerrar consulta
                if (con != null) con.close(); // Cerrar conexión
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return actualizado; // Devuelve verdadero si pudo actualizar
    }

    /**
     * Busca únicamente el número de la cantidad inicial registrada.
     * Útil para saber cuánto stock base había de un producto en un inventario dado.
     */
    public double obtenerStockActual(int idInventario, int idProducto) {
        double stock = 0; // Iniciamos con 0 por defecto
        Connection con = null; // Conexión
        PreparedStatement ps = null; // Para preparar la consulta
        ResultSet rs = null; // Para leer la respuesta
        
        try {
            con = Conexion.getConexion(); // Nos conectamos
            // Pide extraer únicamente el campo cantidad_inicial desde INVENTARIO_DETALLE
            String sql = "SELECT cantidad_inicial FROM INVENTARIO_DETALLE WHERE id_inventario = ? AND id_producto = ?";
            ps = con.prepareStatement(sql); // Prepara sql
            ps.setInt(1, idInventario); // Inyecta el id de inventario
            ps.setInt(2, idProducto); // Inyecta el id de producto
            rs = ps.executeQuery(); // Realiza la búsqueda
            
            if (rs.next()) { // Si encuentra al menos 1 resultado
                stock = rs.getDouble("cantidad_inicial"); // Lo extraemos de la columna y lo guardamos en la variable stock
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener stock actual: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close(); // Cerramos resultados
                if (ps != null) ps.close(); // Cerramos consulta
                if (con != null) con.close(); // Cerramos base de datos
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return stock; // Retornamos ese numero que logramos leer
    }

    /**
     * Muy similar al método listarDetalles, pero además del nombre, también trae el precio unitario del producto.
     * Útil cuando se necesita mostrar u operar cálculos monetarios.
     */
    public java.util.List<com.inventario.model.DetalleInventario> listarDetallesConPrecio(int idInventario) {
        java.util.List<com.inventario.model.DetalleInventario> lista = new java.util.ArrayList<>(); // Creamos la lista donde iran los detalles
        Connection con = null; // Instanciamos la conexión a null
        PreparedStatement ps = null; // Consulta nula
        ResultSet rs = null; // Resultados nulos

        try {
            con = Conexion.getConexion(); // Abrimos conexión DB
            // Selecciona todo de INVENTARIO_DETALLE, y de PRODUCTO pide el nombre y el precio_unitario cruzándolos por id_producto
            String sql = "SELECT d.*, p.nombre, p.precio_unitario " +
                         "FROM INVENTARIO_DETALLE d " +
                         "JOIN PRODUCTO p ON d.id_producto = p.id_producto " +
                         "WHERE d.id_inventario = ?";
            ps = con.prepareStatement(sql); // Se arma el SQL
            ps.setInt(1, idInventario); // Parámetro a buscar
            rs = ps.executeQuery(); // Se lanza la consulta

            while (rs.next()) { // Leemos fila por fila enviada por la tabla
                com.inventario.model.DetalleInventario d = new com.inventario.model.DetalleInventario(); // Se genera objeto para poner los datos de esta fila
                d.setIdDetalle(rs.getInt("id_detalle")); // Llevamos el id detalle
                d.setIdInventario(rs.getInt("id_inventario")); // Llevamos id inventario
                d.setIdProducto(rs.getInt("id_producto")); // Llevamos id de producto
                d.setCantidadInicial(rs.getDouble("cantidad_inicial")); // Llevamos la cantidad guardada
                d.setCantidadFinal(rs.getDouble("cantidad_final")); // Llevamos la cantidad final
                d.setNombreProducto(rs.getString("nombre")); // Extraemos su nombre con el JOIN
                d.setPrecioUnitario(rs.getDouble("precio_unitario")); // Extraemos su precio_unitario con el JOIN
                lista.add(d); // Metemos este objeto relleno a la lista principal
            }
        } catch (SQLException e) {
            System.err.println("Error al listar detalles con precio: " + e.getMessage()); // Mostrar error
        } finally {
            try {
                if (rs != null) rs.close(); // Liberamos variables
                if (ps != null) ps.close(); 
                if (con != null) con.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; // Se retorna la lista
    }

    /**
     * Busca si ya existe un detalle guardado para un producto en este inventario.
     * Si no existe, lo crea automáticamente en 0 en la base de datos y retorna el ID con el que fue creado.
     */
    public int obtenerOCrearDetalle(int idInventario, int idProducto) {
        int idDetalle = -1; // Arranca en -1 como símbolo de que aún no lo tenemos
        Connection con = null; // Variable conexión
        PreparedStatement ps = null; // Variable consulta
        ResultSet rs = null; // Variable respuesta
        try {
            con = Conexion.getConexion(); // Conectar 
            
            // Primero consultamos si en la tabla INVENTARIO_DETALLE ya hay una fila para este inventario y producto
            String sqlBusqueda = "SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_inventario = ? AND id_producto = ?";
            ps = con.prepareStatement(sqlBusqueda); // Pasamos búsqueda
            ps.setInt(1, idInventario); // Seteamos ID de inventario
            ps.setInt(2, idProducto); // Seteamos ID de producto
            rs = ps.executeQuery(); // Ejecutamos la búsqueda
            
            if (rs.next()) { // Si la encontró
                idDetalle = rs.getInt("id_detalle"); // Copiamos el ID de ese registro y terminamos
            } else {
                // Si la consulta no trajo nada, entonces insertamos un registro nuevo desde 0
                // Hacemos el INSERT hacia INVENTARIO_DETALLE donde producto e inventario existen pero con cantidades en cero
                String sqlInsert = "INSERT INTO INVENTARIO_DETALLE (id_inventario, id_producto, cantidad_inicial, cantidad_final) VALUES (?, ?, 0, 0)";
                PreparedStatement psInsert = con.prepareStatement(sqlInsert, java.sql.Statement.RETURN_GENERATED_KEYS); // Le avisamos que queremos la llave primaria de retorno
                psInsert.setInt(1, idInventario); // Seteamos id inventario
                psInsert.setInt(2, idProducto); // Seteamos id producto
                psInsert.executeUpdate(); // Insertamos fila oficialmente
                
                ResultSet rsInsert = psInsert.getGeneratedKeys(); // Solicitamos ese ID auto-numérico que la BD generó
                if (rsInsert.next()) { 
                    idDetalle = rsInsert.getInt(1); // Nos guardamos ese ID
                }
                rsInsert.close(); // Cerramos recolector de llaves
                psInsert.close(); // Cerramos consulta de insertar
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener/crear detalle inventario: " + e.getMessage()); // Print de error en BD
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close(); // Cerrar memoria
                if (ps != null) ps.close(); // Cerrar memoria
                if (con != null) con.close(); // Cortar conexión
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return idDetalle; // Finaliza devolviendo el ID que se logró encontrar o crear nuevo
    }
}
