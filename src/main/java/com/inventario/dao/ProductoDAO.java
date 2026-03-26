package com.inventario.dao;

import com.inventario.util.Conexion;   
import com.inventario.model.Producto;  
import java.sql.Connection;            
import java.sql.PreparedStatement;     
import java.sql.ResultSet;            
import java.sql.SQLException;         
import java.util.ArrayList;           
import java.util.List;                

/**
 * Clase ProductoDAO.
 * 
 * Gestiona todo lo relacionado con la tabla que alberga el catálogo maestro
 * (los productos genéricos) independiente de si hay 0 o 100 de stock.
 */
public class ProductoDAO {

    /**
     * Trae el catálogo de productos con el stock actual de un negocio específico.
     * Utiliza una subconsulta para evitar duplicados por inventarios pasados.
     * 
     * @param idNegocio ID del bar para filtrar el stock activo
     * @return Lista de productos con su stock (o 0 si no tienen)
     */
    /**
     * Trae el catálogo de productos con el stock actual de un negocio específico.
     * Utiliza una subconsulta para evitar duplicados por inventarios pasados.
     */
    public List<Producto> listarProductos(int idNegocio) {
        List<Producto> lista = new ArrayList<>(); // Lista de productos para la página
        Connection con = null;        
        PreparedStatement ps = null;  
        ResultSet rs = null;          
        
        try {
            con = Conexion.getConexion(); // Enlace a la bd
            

            String sql = "SELECT p.*, COALESCE(idx.cantidad_inicial, 0) AS stock_actual " +   // Selecciona todos los campos de PRODUCTO y además calcula el stock_actual (si no hay cantidad, devuelve 0 con COALESCE)

                         "FROM PRODUCTO p " +   // Tabla principal: PRODUCTO con alias "p"

                         "LEFT JOIN (" +   // Hace un LEFT JOIN con una subconsulta (alias idx). Trae todos los productos aunque no tengan inventario activo

                         "    SELECT di.id_producto, di.cantidad_inicial " +   // Subconsulta: selecciona id_producto y cantidad_inicial de INVENTARIO_DETALLE

                         "    FROM INVENTARIO_DETALLE di " +   // Tabla INVENTARIO_DETALLE con alias "di"

                         "    INNER JOIN INVENTARIO i ON di.id_inventario = i.id_inventario " +   // Une INVENTARIO_DETALLE con INVENTARIO, solo si coinciden los id_inventario

                         "    WHERE i.id_negocio = ? AND i.estado = 'activo'" +   // Filtra: solo inventarios del negocio indicado (parámetro ?) y que estén activos

                         ") idx ON p.id_producto = idx.id_producto";   // Relaciona la subconsulta (idx) con PRODUCTO: une por id_producto

                         
            ps = con.prepareStatement(sql);    
            ps.setInt(1, idNegocio); // Inyectamos el bar del usuario
            rs = ps.executeQuery(); // Disparamos la búsqueda
            
            while (rs.next()) { // Recorremos el catálogo resultante
                Producto p = new Producto();
                // Mapeo de columnas de MySQL a variables de Java:
                p.setIdProducto(rs.getInt("id_producto"));
                p.setNombre(rs.getString("nombre"));
                p.setMarca(rs.getString("marca"));
                p.setPrecioUnitario(rs.getDouble("precio_unitario"));
                p.setTipo(rs.getString("tipo"));
                p.setImagen(rs.getString("imagen"));
                p.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                p.setCantidadMedida(rs.getString("cantidad_medida"));
                p.setStok_actual(rs.getDouble("stock_actual")); // Atrapamos el cálculo de la subconsulta
                lista.add(p); // Añadimos al listado final
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage()); 
        } finally {
            try {
                if (rs != null) rs.close();   
                if (ps != null) ps.close();   
                if (con != null) con.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; // Devolvemos el catálogo listo para mostrar en la web
    }

    /**
     * Ingresa en el sistema una fila nueva de maestro producto.
     */
    public boolean registrarProducto(Producto p) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean registrado = false;
        
        try {
            con = Conexion.getConexion(); // Nos conectamos
            
            // CONSULTA SQL (Inserción en Catálogo):
            String sql = "INSERT INTO PRODUCTO (nombre, marca, precio_unitario, tipo, imagen, fecha_vencimiento, cantidad_medida) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre()); // Nombre del líquido/snack
            ps.setString(2, p.getMarca()); // Empresa fabricante
            ps.setDouble(3, p.getPrecioUnitario()); // Costo al público
            ps.setString(4, p.getTipo()); // Categioría (bebida, snack, etc)
            ps.setString(5, p.getImagen()); // Link de la foto
            
            // Lógica para fechas nulas (evita errores SQL si no tiene vencimiento)
            if (p.getFechaVencimiento() != null) {
                ps.setDate(6, p.getFechaVencimiento()); 
            } else {
                ps.setNull(6, java.sql.Types.DATE);     
            }
            
            ps.setString(7, p.getCantidadMedida()); // ej: "330ml", "100gr"
            
            int filas = ps.executeUpdate(); // Ejecutamos la orden
            if (filas > 0) {
                registrado = true; // Si MySQL aceptó el registro
            }
            
        } catch (SQLException e) {
            System.out.println("Error grave en registro de prod: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fallo al registrar producto " + e.getMessage()); 
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return registrado;
    }

    /**
     * Verifica si un producto tiene datos vinculados (ventas o pedidos).
     * Se usa para impedir que se borre si ya tiene historial contable.
     */
    public boolean productoTieneDatos(int idProducto) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean tieneDatos = false;

        try {
            con = Conexion.getConexion();
            
            // CONSULTA SQL DE INTEGRIDAD (Conteo cruzado):
            // 1. SELECT COUNT(*): Cuenta registros de ventas y pedidos.
            // 2. id_inv_detalle IN (...): Busca si el producto está en el inventario de algún bar
            //    que ya haya registrado movimientos (Ventas o Compras).
            String sql = "SELECT " +   // Inicia la consulta SELECT principal

                         "(SELECT COUNT(*) FROM DETALLE_VENTA WHERE id_inv_detalle IN " +   // Subconsulta 1: cuenta cuántos registros hay en DETALLE_VENTA para un id_inv_detalle específico

                         "(SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_producto = ?)) + " +   // Sub-subconsulta: obtiene los id_detalle de INVENTARIO_DETALLE que corresponden al producto indicado (primer parámetro ?)

                         "(SELECT COUNT(*) FROM DETALLE_PEDIDOS WHERE id_inv_detalle IN " +   // Subconsulta 2: cuenta cuántos registros hay en DETALLE_PEDIDOS para esos mismos id_inv_detalle

                         "(SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_producto = ?)) " +   // Sub-subconsulta: nuevamente obtiene los id_detalle de INVENTARIO_DETALLE para el producto indicado (segundo parámetro ?)

                         "AS total_datos";   // El resultado final será la suma de ambas cuentas, con alias "total_datos"

            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idProducto);
            ps.setInt(2, idProducto);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt("total_datos") > 0) {
                tieneDatos = true; // El producto no es nuevo, ya tiene historia
            }
        } catch (SQLException e) {
            System.err.println("Error verificando historial de producto: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return tieneDatos;
    }

    /**
     * Elimina un producto SOLO si no tiene historial vinculado.
     * Primero limpia su rastro en INVENTARIO_DETALLE y luego lo quita del catálogo maestro.
     */
    public boolean eliminarProducto(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;
        
        try {
            con = Conexion.getConexion();
            
            // TRANSACCIÓN: O borramos rastro y maestro, o nada.
            con.setAutoCommit(false); 

            // PASO 1: Eliminar registros de vinculación en inventarios (si están vacíos).
            String sqlDetalleInv = "DELETE FROM INVENTARIO_DETALLE WHERE id_producto = ?";
            ps = con.prepareStatement(sqlDetalleInv);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();

            // PASO 2: Eliminar la existencia del producto del catálogo PRODUCTO.
            String sqlProducto = "DELETE FROM PRODUCTO WHERE id_producto = ?";
            ps = con.prepareStatement(sqlProducto);
            ps.setInt(1, id);
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                eliminado = true;
                con.commit(); // Confirmamos el borrado atómico
                System.out.println("Producto " + id + " eliminado correctamente del catálogo.");
            } else {
                con.rollback(); // Algo salió mal, recuperamos el producto
            }
            
        } catch (SQLException e) {
            System.err.println("Error al intentar eliminar producto: " + e.getMessage());
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return eliminado;
    }

    /**
     * Busca los datos de un único producto a través de su código o ID. 
     */
    public Producto obtenerProducto(int id) {
        Producto p = null; // Caja vacía
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            
            // CONSULTA SQL: Búsqueda exacta por llave primaria.
            String sql = "SELECT * FROM PRODUCTO WHERE id_producto = ?"; 
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) { // Si el servidor encontró el producto
                p = new Producto(); // Llenamos el objeto Java:
                p.setIdProducto(rs.getInt("id_producto"));             
                p.setNombre(rs.getString("nombre"));                   
                p.setMarca(rs.getString("marca"));                     
                p.setPrecioUnitario(rs.getDouble("precio_unitario"));  
                p.setTipo(rs.getString("tipo"));
                p.setImagen(rs.getString("imagen"));
                p.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                p.setCantidadMedida(rs.getString("cantidad_medida"));
            }
        } catch (SQLException e) {
            System.err.println("Error recuperando producto específico: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return p; // Retornamos el producto localizado
    }
    
    /**
     * Modifica los parámetros de un artículo existente en el catálogo.
     */
    public boolean actualizarProducto(Producto p) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizado = false;
        
        try {
            con = Conexion.getConexion();
            
            // CONSULTA SQL (Actualización de Catálogo):
            String sql = "UPDATE PRODUCTO SET nombre = ?, marca = ?, precio_unitario = ?, " +   // Actualiza la tabla PRODUCTO, cambiando nombre, marca y precio_unitario
                        "tipo = ?, imagen = ?, fecha_vencimiento = ?, cantidad_medida = ? " +   // También actualiza tipo, imagen, fecha de vencimiento y cantidad_medida
                        "WHERE id_producto = ?";   // Condición: solo se actualiza el producto cuyo id_producto coincida con el valor dado
            
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre());         
            ps.setString(2, p.getMarca());           
            ps.setDouble(3, p.getPrecioUnitario());  
            ps.setString(4, p.getTipo());            
            ps.setString(5, p.getImagen());          
            
            // Manejo de fecha nula en edición
            if (p.getFechaVencimiento() != null) {
                ps.setDate(6, p.getFechaVencimiento()); 
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            
            ps.setString(7, p.getCantidadMedida());  
            ps.setInt(8, p.getIdProducto()); // Identificador del registro a editar
            
            int filas = ps.executeUpdate(); // Ejecutamos el cambio físico
            if (filas > 0) {
                actualizado = true; // Si MySQL confirmó la edición
            }
            
        } catch (SQLException e) {
            System.err.println("Falla de UPDATE catalogo maestro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return actualizado;
    }

    /**
     * Revisa si un producto con el mismo nombre ya existe (Para evitar productos duplicados).
     */
    public boolean existeNombreProducto(String nombre) {
        boolean existe = false; // Bandera de seguridad
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            
            // CONSULTA SQL (Conteo rápido por nombre):
            String sql = "SELECT COUNT(*) FROM PRODUCTO WHERE nombre = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) { // Si el conteo es mayor a 0
                existe = true; // Alerta: el nombre ya está ocupado
            }
        } catch (SQLException e) {
            System.err.println("Falla de verificación de choque de nombres prod: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return existe; // Informamos si hay choque
    }
}
