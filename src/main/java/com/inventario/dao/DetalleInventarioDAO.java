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
        Connection con = null; // Objeto para la conexión física a MySQL
        PreparedStatement ps = null; // Objeto para preparar la consulta SQL de forma segura
        boolean registrado = false; // Variable de control para saber si se insertó el dato
        
        try {
            con = Conexion.getConexion(); // Obtenemos la conexión del puente (util/Conexion.java)
            
            // CONSULTA SQL: Inserta un nuevo registro en la tabla INVENTARIO_DETALLE.
            // Se especifican las 3 columnas y se usan '?' como marcadores de posición para evitar inyección SQL.
            String sql = "INSERT INTO INVENTARIO_DETALLE (id_inventario, id_producto, cantidad_inicial) VALUES (?, ?, ?)";
            
            ps = con.prepareStatement(sql); // Preparamos el comando en el servidor de BD
            ps.setInt(1, idInventario); // Reemplazamos el 1er '?' con el código del inventario (mes/periodo)
            ps.setInt(2, idProducto); // Reemplazamos el 2do '?' con el código del producto maestro
            ps.setDouble(3, cantidadInicial); // Reemplazamos el 3er '?' con el stock contado físicamente
            
            int filas = ps.executeUpdate(); // Ejecutamos la inserción en la tabla
            registrado = (filas > 0); // Si modificó 1 fila o más, el registro fue exitoso
            
        } catch (SQLException e) {
            System.err.println("Error al insertar detalle inventario: " + e.getMessage()); // Reporte de error en consola
        } finally {
            // BLOQUE FINALLY: Garantiza que la conexión se cierre aunque ocurra un error
            try {
                if (ps != null) ps.close(); // Cerramos el preparador de consultas
                if (con != null) con.close(); // Cerramos el enchufe a la base de datos
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return registrado; // Informamos el resultado (true o false)
    }

    /**
     * Sincroniza el catálogo maestro con el inventario actual.
     * Inserta en INVENTARIO_DETALLE cualquier producto que exista en el catálogo (PRODUCTO)
     * pero que no tenga aún un registro en este inventario.
     * 
     * @param idInventario ID del periodo a sincronizar
     */
    public void sincronizarProductos(int idInventario) {
        Connection con = null; // Variable para gestionar la conexión
        PreparedStatement ps = null; // Variable para gestionar el SQL
        try {
            con = Conexion.getConexion(); // Abrimos conexión segura
            
            // CONSULTA SQL COMPLEJA (Insert con Subconsulta):
            // 1. INSERT INTO ... SELECT: Toma datos de una tabla (PRODUCTO) y los inyecta en otra (INVENTARIO_DETALLE).
            // 2. FROM PRODUCTO p: Selecciona todos los productos registrados en el sistema dándoles el alias 'p'.
            // 3. WHERE ... NOT IN: Filtra solo los productos que NO estén ya presentes en este inventario específico.
            // 4. Subconsulta (SELECT d.id_producto...): Busca los productos ya registrados en el inventario actual (alias 'd').
            String sql = "INSERT INTO INVENTARIO_DETALLE (id_inventario, id_producto, cantidad_inicial, cantidad_final) " +
                         "SELECT ?, p.id_producto, 0, 0 " +
                         "FROM PRODUCTO p " +
                         "WHERE p.id_producto NOT IN (" +
                         "    SELECT d.id_producto " +
                         "    FROM INVENTARIO_DETALLE d " +
                         "    WHERE d.id_inventario = ?" +
                         ")";
            
            ps = con.prepareStatement(sql); // Preparamos la operación masiva
            ps.setInt(1, idInventario); // Pasamos el ID del inventario actual para el SELECT
            ps.setInt(2, idInventario); // Pasamos el ID del inventario actual para el filtro NOT IN
            
            int filasInsertadas = ps.executeUpdate(); // Ejecutamos la sincronización
            if (filasInsertadas > 0) {
                System.out.println("Sincronización: Se añadieron " + filasInsertadas + " productos nuevos al inventario " + idInventario);
            }
        } catch (SQLException e) {
            System.err.println("Error sincronizando productos en inventario: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close(); // Limpiamos recursos
                if (con != null) con.close(); // Soltamos conexión
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Trae todos los detalles de un inventario con el nombre del producto incluido.
     * Une la tabla INVENTARIO_DETALLE con la tabla PRODUCTO para poder sacar el nombre.
     */
    public java.util.List<com.inventario.model.DetalleInventario> listarDetalles(int idInventario) {
        java.util.List<com.inventario.model.DetalleInventario> lista = new java.util.ArrayList<>(); // Contenedor de resultados
        Connection con = null; // Enlace a BD
        PreparedStatement ps = null; // Preparador SQL
        ResultSet rs = null; // Almacén de filas devueltas por MySQL
        
        try {
            con = Conexion.getConexion(); // Nos conectamos
            
            // CONSULTA SQL (Selección con Unión/Join):
            // 1. SELECT d.*, p.nombre: Trae todas las columnas del Detalle (d) y solo el Nombre del Producto (p).
            // 2. FROM INVENTARIO_DETALLE d: Tabla principal de donde sacamos stock e IDs.
            // 3. JOIN PRODUCTO p ON d.id_producto = p.id_producto: Cruza la tabla detalle con la tabla de nombres de productos.
            // 4. WHERE d.id_inventario = ?: Filtra únicamente los productos del inventario que estamos consultando.
            // 5. ORDER BY p.nombre ASC: Ordena alfabéticamente de la A a la Z para facilitar la vista al usuario.
            String sql = "SELECT d.*, p.nombre FROM INVENTARIO_DETALLE d " +
                         "JOIN PRODUCTO p ON d.id_producto = p.id_producto " +
                         "WHERE d.id_inventario = ? " +
                         "ORDER BY p.nombre ASC"; 
            
            ps = con.prepareStatement(sql); // Cargamos el SQL
            ps.setInt(1, idInventario); // Inyectamos el ID del filtro
            rs = ps.executeQuery(); // Disparamos la búsqueda
            
            while (rs.next()) { // Recorremos fila por fila mientras MySQL tenga datos
                com.inventario.model.DetalleInventario d = new com.inventario.model.DetalleInventario(); // Creamos el objeto molde
                // Rellenamos el objeto con los datos de las columnas de la BD
                d.setIdDetalle(rs.getInt("id_detalle")); 
                d.setIdInventario(rs.getInt("id_inventario")); 
                d.setIdProducto(rs.getInt("id_producto")); 
                d.setCantidadInicial(rs.getDouble("cantidad_inicial")); 
                d.setCantidadFinal(rs.getDouble("cantidad_final")); 
                d.setNombreProducto(rs.getString("nombre")); // Dato obtenido gracias al JOIN
                lista.add(d); // Guardamos en la lista para enviarla a la Web
            }
        } catch (SQLException e) {
            System.err.println("Error al listar detalles inventario: " + e.getMessage()); 
        } finally {
            try {
                if (rs != null) rs.close(); // Cerramos el flujo de datos
                if (ps != null) ps.close(); // Cerramos la orden SQL
                if (con != null) con.close(); // Apagamos la conexión
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; // Enviamos la recopilación completa de productos
    }

    /**
     * Actualiza solo la cantidad final de un producto en el inventario.
     */
    public boolean actualizarCantidadFinal(int idInventario, int idProducto, double cantidadFinal) {
        Connection con = null; // Objeto de enlace
        PreparedStatement ps = null; // Objeto de comando
        boolean actualizado = false; // Variable de estado
        
        try {
            con = Conexion.getConexion(); // Conexión activa
            
            // CONSULTA SQL (Actualización):
            // 1. UPDATE INVENTARIO_DETALLE: Indica la tabla donde vamos a modificar datos.
            // 2. SET cantidad_final = ?: Establece el nuevo valor del stock final contado por el usuario.
            // 3. WHERE id_inventario = ? AND id_producto = ?: Restricción vital para no borrar el stock de otros meses u otros productos.
            String sql = "UPDATE INVENTARIO_DETALLE SET cantidad_final = ? WHERE id_inventario = ? AND id_producto = ?";
            
            ps = con.prepareStatement(sql); // Preparamos la actualización
            ps.setDouble(1, cantidadFinal); // Nuevo stock final
            ps.setInt(2, idInventario); // Inventario objetivo
            ps.setInt(3, idProducto); // Producto objetivo
            
            int filas = ps.executeUpdate(); // Realizamos el cambio físico en el disco duro de la BD
            actualizado = (filas > 0); // Si hubo cambios, devolvemos éxito
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cantidad final: " + e.getMessage()); 
        } finally {
            try {
                if (ps != null) ps.close(); // Finalización de recurso
                if (con != null) con.close(); // Finalización de conexión
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return actualizado; // Resultado a la lógica del servidor
    }

    /**
     * Busca únicamente el número de la cantidad inicial registrada.
     */
    public double obtenerStockActual(int idInventario, int idProducto) {
        double stock = 0; // Iniciamos en cero por defecto
        Connection con = null; 
        PreparedStatement ps = null; 
        ResultSet rs = null; 
        
        try {
            con = Conexion.getConexion(); // Nos enlazamos a MySQL
            
            // CONSULTA SQL: Selecciona únicamente la columna 'cantidad_inicial' filtrando por ID de inventario y producto.
            String sql = "SELECT cantidad_inicial FROM INVENTARIO_DETALLE WHERE id_inventario = ? AND id_producto = ?";
            
            ps = con.prepareStatement(sql); 
            ps.setInt(1, idInventario); 
            ps.setInt(2, idProducto); 
            rs = ps.executeQuery(); // Ejecutamos la lectura rápida
            
            if (rs.next()) { // Si el cruce de IDs existe en la bodega
                stock = rs.getDouble("cantidad_inicial"); // Extraemos el número decimal
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener stock actual: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close(); 
                if (ps != null) ps.close(); 
                if (con != null) con.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return stock; // Retornamos el stock físico actual
    }

    /**
     * Muy similar al método listarDetalles, pero además del nombre, también trae el precio unitario del producto.
     */
    public java.util.List<com.inventario.model.DetalleInventario> listarDetallesConPrecio(int idInventario) {
        java.util.List<com.inventario.model.DetalleInventario> lista = new java.util.ArrayList<>(); // Contenedor
        Connection con = null; 
        PreparedStatement ps = null; 
        ResultSet rs = null; 

        try {
            con = Conexion.getConexion(); // Conexión abierta
            
         
            String sql = "SELECT d.*, p.nombre, p.precio_unitario " +   // Selecciona todos los campos de INVENTARIO_DETALLE (alias d), más el nombre y precio_unitario del producto

                         "FROM INVENTARIO_DETALLE d " +   // Tabla principal: INVENTARIO_DETALLE con alias "d"

                         "JOIN PRODUCTO p ON d.id_producto = p.id_producto " +   // Une INVENTARIO_DETALLE con PRODUCTO, relacionando por id_producto (para obtener datos del producto)

                         "WHERE d.id_inventario = ? " +   // Filtra: solo muestra los detalles que pertenecen al inventario indicado (parámetro ?)

                         "ORDER BY p.nombre ASC";   // Ordena los resultados por el nombre del producto en orden ascendente (A-Z)

            
            ps = con.prepareStatement(sql); 
            ps.setInt(1, idInventario); 
            rs = ps.executeQuery(); 

            while (rs.next()) { 
                com.inventario.model.DetalleInventario d = new com.inventario.model.DetalleInventario(); 
                d.setIdDetalle(rs.getInt("id_detalle")); 
                d.setIdInventario(rs.getInt("id_inventario")); 
                d.setIdProducto(rs.getInt("id_producto")); 
                d.setCantidadInicial(rs.getDouble("cantidad_inicial")); 
                d.setCantidadFinal(rs.getDouble("cantidad_final")); 
                d.setNombreProducto(rs.getString("nombre")); 
                d.setPrecioUnitario(rs.getDouble("precio_unitario")); // Cargamos el precio para cálculos de valor de inventario
                lista.add(d); 
            }
        } catch (SQLException e) {
            System.err.println("Error al listar detalles con precio: " + e.getMessage()); 
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
     * Busca si ya existe un detalle guardado para un producto en este inventario.
     * Si no existe, lo crea automáticamente en 0 en la base de datos y retorna el ID con el que fue creado.
     */
    public int obtenerOCrearDetalle(int idInventario, int idProducto) {
        int idDetalle = -1; // ID invádilo por defecto si algo falla
        Connection con = null; 
        PreparedStatement ps = null; 
        ResultSet rs = null; 
        try {
            con = Conexion.getConexion(); // Enlace
            
            // PRIMERA CONSULTA: Verificamos si la fila ya existe en INVENTARIO_DETALLE
            String sqlBusqueda = "SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_inventario = ? AND id_producto = ?";
            ps = con.prepareStatement(sqlBusqueda); 
            ps.setInt(1, idInventario); 
            ps.setInt(2, idProducto); 
            rs = ps.executeQuery(); 
            
            if (rs.next()) { // ¡Existe!
                idDetalle = rs.getInt("id_detalle"); // Recuperamos el ID actual
            } else {
                // NO EXISTE: Procedemos a crear el registro de "casilla vacía" (0 inicial, 0 final)
                // Usamos RETURN_GENERATED_KEYS para atrapar el ID autoincremental que genere MySQL
                String sqlInsert = "INSERT INTO INVENTARIO_DETALLE (id_inventario, id_producto, cantidad_inicial, cantidad_final) VALUES (?, ?, 0, 0)";
                PreparedStatement psInsert = con.prepareStatement(sqlInsert, java.sql.Statement.RETURN_GENERATED_KEYS); 
                psInsert.setInt(1, idInventario); 
                psInsert.setInt(2, idProducto); 
                psInsert.executeUpdate(); // Insertamos
                
                ResultSet rsInsert = psInsert.getGeneratedKeys(); // Pedimos el ID recién nacido
                if (rsInsert.next()) { 
                    idDetalle = rsInsert.getInt(1); // ¡Capturado!
                }
                rsInsert.close(); // Cerramos objetos temporales internos
                psInsert.close(); 
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener/crear detalle inventario: " + e.getMessage()); 
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close(); 
                if (ps != null) ps.close(); 
                if (con != null) con.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return idDetalle; // Retornamos el ID (existente o recién creado)
    }
}
