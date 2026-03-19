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
     * Trae un listado completo de todos los productos del catálogo.
     * Lista cada fila de la tabla PRODUCTO.
     */
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>(); // Lista donde se pondrán los elementos a presentar
        Connection con = null;        
        PreparedStatement ps = null;  
        ResultSet rs = null;          
        
        try {
            con = Conexion.getConexion(); // Nos asociamos al driver BD
            String sql = "SELECT * FROM PRODUCTO"; // Simplemente trae todo el catálogo
            ps = con.prepareStatement(sql);        
            rs = ps.executeQuery();                
            
            while (rs.next()) { // Recorre línea a línea el catálogo retornado por SQL
                Producto p = new Producto(); // Creamos la representación del producto
                p.setIdProducto(rs.getInt("id_producto"));             // ID
                p.setNombre(rs.getString("nombre"));                   // Empanadas
                p.setMarca(rs.getString("marca"));                     // Ricuras
                p.setPrecioUnitario(rs.getDouble("precio_unitario"));  // Costo por unidad
                p.setTipo(rs.getString("tipo"));                       // Comestible, Varios, Aseo, Bebida
                p.setImagen(rs.getString("imagen"));                   // Nombre de la foto subida
                p.setFechaVencimiento(rs.getDate("fecha_vencimiento"));// Su caducidad (Ojalá no estén vencidas)
                p.setCantidadMedida(rs.getString("cantidad_medida"));  // 150gr, 1L, etc.
                lista.add(p); // Y agregalo a la Lista
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage()); 
        } finally { // Cierre basurero
            try {
                if (rs != null) rs.close();   
                if (ps != null) ps.close();   
                if (con != null) con.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; // Mandar al que llamó la variable
    }

    /**
     * Ingresa en el sistema una fila nueva de maestro producto.
     */
    public boolean registrarProducto(Producto p) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean registrado = false; // Flag de confirmacion de guardado
        
        try {
            con = Conexion.getConexion(); // Conexion DB
            // Preparamos todos sus campos posibles
            String sql = "INSERT INTO PRODUCTO (nombre, marca, precio_unitario, tipo, imagen, fecha_vencimiento, cantidad_medida) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            ps = con.prepareStatement(sql); // Enganchar conector
            ps.setString(1, p.getNombre());          // Nombre
            ps.setString(2, p.getMarca());           // Fabrica
            ps.setDouble(3, p.getPrecioUnitario());  // $$
            ps.setString(4, p.getTipo());            // Clase
            ps.setString(5, p.getImagen());          // Archivo visual
            
            if (p.getFechaVencimiento() != null) { // Alimento, con fecha
                ps.setDate(6, p.getFechaVencimiento()); 
            } else { // Si no era un producto perecedero o se dejó vacío un balde, la ingresa con null
                ps.setNull(6, java.sql.Types.DATE);     
            }
            
            ps.setString(7, p.getCantidadMedida());  // Unidad magnitud
            
            int filas = ps.executeUpdate(); // Confirmar escritura a disco
            if (filas > 0) {
                registrado = true; // Todo bien
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
        return registrado; // Return final
    }

    /**
     * Tarea minuciosa de borrar un producto del CATALOGO GENERAL.
     * Borrar al papá borrará también a todos los hijos debido a la base de datos (Llaves foráneas).
     * Esto busca en las ventas que incluyeron este producto, los pedidos y todo y lo desaparece para no dejar referencias muertas.
     */
    public boolean eliminarProducto(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;
        
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // Activamos la Transacción para garantizar seguridad total (todo se hace al final con .commit)
            
            // 1. Borrar todas las ocasiones en que el producto se le facturó (vendio) a un cliente 
            // Subconsulta IN por si el producto es parte de muchos detalles
            String sqlDetalleVenta = "DELETE FROM DETALLE_VENTA WHERE id_inv_detalle IN " +
                                     "(SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_producto = ?)";
            ps = con.prepareStatement(sqlDetalleVenta);
            ps.setInt(1, id); 
            ps.executeUpdate();
            ps.close(); // Liberamos 1
            
            // 2. Borrar las ocasiones en que se armó pedido pidiendo ESTE producto
            String sqlDetallePedidos = "DELETE FROM DETALLE_PEDIDOS WHERE id_inv_detalle IN " +
                                       "(SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_producto = ?)";
            ps = con.prepareStatement(sqlDetallePedidos);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close(); // Liberamos 2
            
            // 3. Borrar el stock del producto de cualquier negocio
            String sqlDetalleInv = "DELETE FROM INVENTARIO_DETALLE WHERE id_producto = ?";
            ps = con.prepareStatement(sqlDetalleInv);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close(); // Liberamos 3
            
            // 4. Si logramos limpiar todo el rastro de la familia, eliminamos al final la tarjeta maestra del producto
            String sqlProducto = "DELETE FROM PRODUCTO WHERE id_producto = ?";
            ps = con.prepareStatement(sqlProducto);
            ps.setInt(1, id);
            
            int filas = ps.executeUpdate(); // Este es el que vale
            if (filas > 0) { // Si sí hubo limpieza...
                eliminado = true; // Asignar meta en verdadero OK
            }
            
            con.commit(); // Confirmar ahora sí la desaparición oficial
            System.out.println("Producto completamente limpiado y borrado del sistema.");
            
        } catch (SQLException e) { // Pánico
            System.err.println("Falla crítica borrando producto en cascada: " + e.getMessage());
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // Que Dios se apiade y revierta hasta el subtotal que se alteró en las facturas al borrarlo, dejar todo intocable
            } catch (SQLException ex) { ex.printStackTrace(); }
        } finally { // Liberar pesada memoria
            try {
                if (ps != null) ps.close();
                if (con != null) {
                    con.setAutoCommit(true); // Terminar transaction
                    con.close(); // Dejar libre server HTTP
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return eliminado; // Mandar Boolean Veredicto (lo borré o no lo pude borrar)
    }

    /**
     * Busca los datos de un único producto a través de su código o ID. 
     * Retorna una caja armadita con toda su información para pintarlo en formularios que se necesite arreglar.
     */
    public Producto obtenerProducto(int id) {
        Producto p = null; // Primero null previene errores si justo el usuario pide uno borrado 
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion(); // Conectar
            String sql = "SELECT * FROM PRODUCTO WHERE id_producto = ?";  // Traete mi catálogo de este único producto
            ps = con.prepareStatement(sql);
            ps.setInt(1, id); // Dale el identificador
            rs = ps.executeQuery(); // Exige
            
            if (rs.next()) { // Si sí había ese código
                p = new Producto(); // Ahora si construyo la maquina producto en java
                // Y le voy vaciando el embudo
                p.setIdProducto(rs.getInt("id_producto"));             
                p.setNombre(rs.getString("nombre"));                   
                p.setMarca(rs.getString("marca"));                     
                p.setPrecioUnitario(rs.getDouble("precio_unitario"));  
                p.setTipo(rs.getString("tipo"));
                p.setImagen(rs.getString("imagen"));
                p.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                p.setCantidadMedida(rs.getString("cantidad_medida"));
            }
        } catch (SQLException e) { // Falló query en bd
            System.err.println("Error recuperando producto específico: " + e.getMessage());
        } finally { // Bye bye recursos
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return p; // Sale el producto del inventario para su uso en pantalla (o null si era mito)
    }
    
    /**
     * Operación final, modifica uno o más de uno de los parámetros de nuestro 
     * articulo que existe en el catálogo maestro. Actualiza la tabla PRODUCTO.
     */
    public boolean actualizarProducto(Producto p) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizado = false; // Como siempre veredicto
        
        try {
            con = Conexion.getConexion(); // Conexion 
            // Hacer UPDATE en todos sus campos basado en el ID final (WHERE id_producto = ?)
            String sql = "UPDATE PRODUCTO SET nombre = ?, marca = ?, precio_unitario = ?, " +
                         "tipo = ?, imagen = ?, fecha_vencimiento = ?, cantidad_medida = ? " +
                         "WHERE id_producto = ?"; 
            
            ps = con.prepareStatement(sql); // Instalar String de busqueda
            ps.setString(1, p.getNombre());         
            ps.setString(2, p.getMarca());           
            ps.setDouble(3, p.getPrecioUnitario());  
            ps.setString(4, p.getTipo());            
            ps.setString(5, p.getImagen());          
            
            if (p.getFechaVencimiento() != null) { // Por si actualizaron agregando fechitas o el prod caducaba
                ps.setDate(6, p.getFechaVencimiento()); 
            } else {
                ps.setNull(6, java.sql.Types.DATE); // De pronto le borraron su vencimiento para ponerlo perenne
            }
            
            ps.setString(7, p.getCantidadMedida());  
            ps.setInt(8, p.getIdProducto());          // Con este identificamos a quien actualizar.
            
            int filas = ps.executeUpdate(); // Se guardo cambio
            if (filas > 0) { // Si SQL confirmara renglon editado OK
                actualizado = true; // Todo good
                System.out.println("El producto con su respectiva ID ha quedado remodeleado.");
            }
            
        } catch (SQLException e) { // Si reventaba base de datos por violaciones foraneas (Que pusiera tipo en nulo ej)
            System.err.println("Falla de UPDATE catalogo maestro: " + e.getMessage());
            e.printStackTrace();
        } finally { // Libere recursos DB
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return actualizado; // Devuelve la confirmación.
    }

    /**
     * Revisa de forma rápida si un producto (con el mismo exacto nombre) ya se hallaba en base.
     * Es bueno para evitar clonar el mismo artículo por accidente antes de hacer Inserciones.
     */
    public boolean existeNombreProducto(String nombre) {
        boolean existe = false; // Empezamos negativos
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion(); // Conex.
            // Contamos cuánto de ese nombre hemos ingresado. 
            String sql = "SELECT COUNT(*) FROM PRODUCTO WHERE nombre = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery(); // Disparamos query count
            if (rs.next() && rs.getInt(1) > 0) { // Si el primer valor del COUNT(*) rebotaba más de 0...
                existe = true; // ...sí existía ese nombre ya en alguna parte. True.
            }
        } catch (SQLException e) { // Pos si caemos
            System.err.println("Falla de verificación de choque de nombres prod: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return existe; // Decimos si al final existió
    }
}
