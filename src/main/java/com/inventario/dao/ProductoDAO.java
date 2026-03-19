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
 * Clase Patrón DAO (Data Access Object): ProductoDAO.
 * 
 * Controlador de persistencia asignado a la Entidad Abstracta relacional (PRODUCTO).
 * Actúa como orquestador de memoria de las consultas DML, encapsulando 
 * la lógica abstracta del modelo de objetos hacia el modelo de dependencias de la tabla.
 */
public class ProductoDAO {

    /**
     * Algoritmo Coleccionador Escalar Iterativo (Getter Colecciones Dinámicas).
     * 
     * Constructor múltiple de arreglos estructurados POJO. Genera una consulta relacional
     * masiva de las propiedades almacenadas y aplica los Setters abstractos 
     * iterando por cada nodo resultante (Filas) retornando la lista ArrayList completa en memoria.
     */
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>(); 
        Connection con = null;        
        PreparedStatement ps = null;  
        ResultSet rs = null;          
        
        try {
            con = Conexion.getConexion(); 
            String sql = "SELECT * FROM PRODUCTO"; 
            ps = con.prepareStatement(sql);        
            rs = ps.executeQuery();                
            
            while (rs.next()) {
                Producto p = new Producto(); 
                p.setIdProducto(rs.getInt("id_producto"));             
                p.setNombre(rs.getString("nombre"));                   
                p.setMarca(rs.getString("marca"));                     
                p.setPrecioUnitario(rs.getDouble("precio_unitario"));  
                p.setTipo(rs.getString("tipo"));                       
                p.setImagen(rs.getString("imagen"));                   
                p.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                p.setCantidadMedida(rs.getString("cantidad_medida"));  
                lista.add(p); 
            }
        } catch (SQLException e) {
            System.err.println("Error constraint context loop: " + e.getMessage()); 
        } finally {
            try {
                if (rs != null) rs.close();   
                if (ps != null) ps.close();   
                if (con != null) con.close(); 
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lista; 
    }

    /**
     * Setter Relacional Simple Unitario.
     * 
     * Subrutina abstracta de inserciones mediante Inyección Extendida de parámetros.
     * Genera una manipulación de la Entidad relacional invocando los Getters provenientes 
     * del modelo Producto POJO abstracto parametrizado en su firma.
     */
    public boolean registrarProducto(Producto p) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean registrado = false; 
        
        try {
            con = Conexion.getConexion();
            String sql = "INSERT INTO PRODUCTO (nombre, marca, precio_unitario, tipo, imagen, fecha_vencimiento, cantidad_medida) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre());         
            ps.setString(2, p.getMarca());           
            ps.setDouble(3, p.getPrecioUnitario());  
            ps.setString(4, p.getTipo());            
            ps.setString(5, p.getImagen());          
            
            if (p.getFechaVencimiento() != null) {
                ps.setDate(6, p.getFechaVencimiento()); 
            } else {
                ps.setNull(6, java.sql.Types.DATE);     
            }
            
            ps.setString(7, p.getCantidadMedida());  
            
            int filas = ps.executeUpdate(); 
            if (filas > 0) {
                registrado = true; 
            }
            
        } catch (SQLException e) {
            System.out.println("Error Data Exception Insert Constraint: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error Exception Limit Bounds: " + e.getMessage()); 
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return registrado; 
    }

    /**
     * Mutador de Destrucción Estructurado Múltiple (Delete Constraints Bounds Handler).
     * 
     * Transaction Unit Method para deconstrucción abstracta del nivel Entidad Superior hacia Nivel Dependencia (Cascada).
     * Implementa lógica Rollback Atómica en caso de un error general a lo largo del proceso.
     */
    public boolean eliminarProducto(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;
        
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); 
            
            String sqlDetalleVenta = "DELETE FROM DETALLE_VENTA WHERE id_inv_detalle IN " +
                                     "(SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_producto = ?)";
            ps = con.prepareStatement(sqlDetalleVenta);
            ps.setInt(1, id); 
            ps.executeUpdate();
            ps.close();
            
            String sqlDetallePedidos = "DELETE FROM DETALLE_PEDIDOS WHERE id_inv_detalle IN " +
                                       "(SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_producto = ?)";
            ps = con.prepareStatement(sqlDetallePedidos);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            
            String sqlDetalleInv = "DELETE FROM INVENTARIO_DETALLE WHERE id_producto = ?";
            ps = con.prepareStatement(sqlDetalleInv);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            
            String sqlProducto = "DELETE FROM PRODUCTO WHERE id_producto = ?";
            ps = con.prepareStatement(sqlProducto);
            ps.setInt(1, id);
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                eliminado = true;
            }
            
            con.commit(); 
            System.out.println("Property Object Constraints removed.");
            
        } catch (SQLException e) {
            System.err.println("Transaction Rollback Failed Constraint: " + e.getMessage());
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); 
            } catch (SQLException ex) { ex.printStackTrace(); }
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
     * Módulo Getter Objeto Singular (Mapper Unitario).
     * 
     * Implementa un Request Abstraction SQL que genera un Objeto constructor mapeando las 
     * propiedades iteradas desde el Data Bounds de una única fila usando Setters Inyectados (Nullable).
     */
    public Producto obtenerProducto(int id) {
        Producto p = null; 
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            String sql = "SELECT * FROM PRODUCTO WHERE id_producto = ?"; 
            ps = con.prepareStatement(sql);
            ps.setInt(1, id); 
            rs = ps.executeQuery();
            
            if (rs.next()) { 
                p = new Producto();
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
            System.err.println("Mapping Property Error context: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return p; 
    }
    
    /**
     * Getter / Setter Mutacional Híbrido.
     * 
     * Operador Data Bounds SQL del nivel UPDATE. Recibe un objeto Entidad y transfiere 
     * cada uno de los atributos instanciados como Mutador de fila de memoria y persistencia real.
     */
    public boolean actualizarProducto(Producto p) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizado = false;
        
        try {
            con = Conexion.getConexion();
            String sql = "UPDATE PRODUCTO SET nombre = ?, marca = ?, precio_unitario = ?, " +
                         "tipo = ?, imagen = ?, fecha_vencimiento = ?, cantidad_medida = ? " +
                         "WHERE id_producto = ?"; 
            
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre());         
            ps.setString(2, p.getMarca());           
            ps.setDouble(3, p.getPrecioUnitario());  
            ps.setString(4, p.getTipo());            
            ps.setString(5, p.getImagen());          
            
            if (p.getFechaVencimiento() != null) {
                ps.setDate(6, p.getFechaVencimiento()); 
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            
            ps.setString(7, p.getCantidadMedida());  
            ps.setInt(8, p.getIdProducto());          
            
            int filas = ps.executeUpdate(); 
            if (filas > 0) {
                actualizado = true;
                System.out.println("Object state Mutation Property Updated.");
            }
            
        } catch (SQLException e) {
            System.err.println("Object Setter mutation constraint bounds limits failed: " + e.getMessage());
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
     * Módulo Factory Consultor Iterador Escalar de Strings.
     * 
     * Accionador Abstracto subrutina Boolean Checker. Utiliza Count y condicional lógico boolean.
     */
    public boolean existeNombreProducto(String nombre) {
        boolean existe = false;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            String sql = "SELECT COUNT(*) FROM PRODUCTO WHERE nombre = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                existe = true;
            }
        } catch (SQLException e) {
            System.err.println("Property limits bounds count boolean failed: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return existe;
    }
}
