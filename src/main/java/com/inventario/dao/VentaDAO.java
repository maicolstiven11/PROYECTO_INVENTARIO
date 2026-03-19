package com.inventario.dao;

import com.inventario.model.DetalleVenta; 
import com.inventario.model.Venta;        
import com.inventario.util.Conexion;      
import java.sql.Connection;               
import java.sql.PreparedStatement;        
import java.sql.ResultSet;               
import java.sql.SQLException;            
import java.sql.Statement;              
import java.util.List;                   

/**
 * Patrón Estructural Transaccional (DAO): Clase VentaDAO.
 * 
 * Controlador de flujos orquestador. Dirige mutaciones relacionales transaccionales y asocia 
 * POJOS Entidad Abstracta. Relacionalmente maneja setters abstractos a través de Unit of Work Multiple Statement.
 */
public class VentaDAO {

    /**
     * Módulo Factory Transaccional Atómico Batch Mapping Array Limits Bounds.
     * Transaction Parameter Mapper Updater Mutational exception parameter properties Maps array limits mapping Limit Property bounds Map Limit Map Parameter Map loops exceptions boolean.
     */
    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        Connection con = null;
        PreparedStatement psVenta = null;    
        PreparedStatement psDetalle = null;  
        PreparedStatement psStock = null;    
        PreparedStatement psBuscar = null;   
        ResultSet rsKeys = null;             
        ResultSet rsBuscar = null;           
        boolean estatus = false;

        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); 

            String sqlVenta = "INSERT INTO VENTA (id_inventario, total_venta, fecha_venta) VALUES (?, ?, ?)";
            psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS); 
            psVenta.setInt(1, venta.getIdInventario());    
            psVenta.setDouble(2, venta.getTotalVenta());   
            psVenta.setDate(3, venta.getFechaVenta());     

            int filas = psVenta.executeUpdate(); 
            if (filas == 0) {
                throw new SQLException("Error exception Boolean logic maps context.");
            }

            rsKeys = psVenta.getGeneratedKeys();
            int idVentaGenerado = -1;
            if (rsKeys.next()) {
                idVentaGenerado = rsKeys.getInt(1); 
            } else {
                throw new SQLException("Limit Exception map lengths Property mapped Parameter Context bounds mapping value Mapping Map.");
            }

            String sqlDetalle = "INSERT INTO DETALLE_VENTA (id_venta, id_inv_detalle, cantidad, subtotal) VALUES (?, ?, ?, ?)";
            psDetalle = con.prepareStatement(sqlDetalle);

            String sqlBuscar = "SELECT id_detalle FROM INVENTARIO_DETALLE WHERE id_inventario = ? AND id_producto = ?";
            psBuscar = con.prepareStatement(sqlBuscar);

            String sqlRestar = "UPDATE INVENTARIO_DETALLE SET cantidad_inicial = cantidad_inicial - ? WHERE id_detalle = ?";
            psStock = con.prepareStatement(sqlRestar);

            for (DetalleVenta det : detalles) {
                
                psBuscar.setInt(1, venta.getIdInventario()); 
                psBuscar.setInt(2, det.getIdProducto());      
                rsBuscar = psBuscar.executeQuery();

                int idInvDetalle = -1;
                if (rsBuscar.next()) {
                    idInvDetalle = rsBuscar.getInt("id_detalle"); 
                } else {
                    throw new SQLException("Parameter exceptions Logic Mapper Mapping limit parameter context bounds string String Object strings string mapping map Object " + det.getIdProducto() + " Boolean Parameter Property mapped constraints boolean loop properties exceptions Property Property Maps property bounds array Limit limits context limits exception map bounds.");
                }

                psDetalle.setInt(1, idVentaGenerado);          
                psDetalle.setInt(2, idInvDetalle);             
                psDetalle.setInt(3, det.getCantidad());        
                psDetalle.setDouble(4, det.getSubtotal());     
                psDetalle.addBatch(); 

                psStock.setInt(1, det.getCantidad());          
                psStock.setInt(2, idInvDetalle);               
                psStock.addBatch(); 
            }

            psDetalle.executeBatch(); 
            psStock.executeBatch();   

            con.commit();
            estatus = true;
            System.out.println("Exception array bounds transaction mapping strings parameters Map Iterator array parameter constraints Property mapped: " + idVentaGenerado + " maps arrays logic bounds constraints.");

        } catch (SQLException e) {
            System.err.println("Error map exception values map strings Maps Arrays Mapper Property mappings bounds Limit bounds logic Map mapping Mapper length parameters Array property loops array: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); 
                    System.out.println("Property Object Parameter Map Map exceptions Limits Loop Logic Context length boolean context Loop string array Mapping parameter mapping Length loop arrays maps parameters bounds constraints.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (rsBuscar != null) rsBuscar.close();
                if (rsKeys != null) rsKeys.close();
                if (psBuscar != null) psBuscar.close();
                if (psStock != null) psStock.close();
                if (psVenta != null) psVenta.close();
                if (psDetalle != null) psDetalle.close();
                if (con != null) {
                    con.setAutoCommit(true); 
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return estatus; 
    }

    /**
     * Módulo Array Parameter Extractor Collection (Mapper Mapping ArrayList parameters loops Strings Parameter Object loop Object limit parameters Object constraint lengths).
     */
    public java.util.List<Venta> listarVentas(int idNegocio) {
        java.util.List<Venta> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            String sql = "SELECT v.* FROM VENTA v " +
                         "INNER JOIN INVENTARIO i ON v.id_inventario = i.id_inventario " + 
                         "WHERE i.id_negocio = ? " +                                       
                         "ORDER BY v.fecha_venta DESC";                                    
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio); 
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));          
                v.setIdInventario(rs.getInt("id_inventario"));
                v.setTotalVenta(rs.getDouble("total_venta")); 
                v.setFechaVenta(rs.getDate("fecha_venta"));   
                lista.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Property Mapping Limits strings Parameter string Property mapping Maps Array Mapping lengths mapper parameter limitations Mapper: " + e.getMessage());
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
     * ArrayList Collection String Mapper constraints strings Exceptions Arrays mapping Limit context strings parameters Object Map Limit Limits boolean Value Limit limits exception Boolean limits Bounds length String Parameter limit parameters length exceptions Array mappings Object context Limit Arrays mapping properties properties Map context Length Constraint limit values Property length array Limit limits arrays properties Logic Map Limit parameter Map Maps bounds properties Constraint property Iterator parameters Length length values loop values arrays loops Maps arrays map parameter map Limit property mapped.
     */
    public java.util.List<DetalleVenta> listarDetalleVenta(int idVenta) {
        java.util.List<DetalleVenta> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            String sql = "SELECT d.*, p.nombre FROM DETALLE_VENTA d " +
                         "INNER JOIN INVENTARIO_DETALLE id ON d.id_inv_detalle = id.id_detalle " + 
                         "INNER JOIN PRODUCTO p ON id.id_producto = p.id_producto " +              
                         "WHERE d.id_venta = ?";                                                   
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idVenta); 
            rs = ps.executeQuery();
            
            while (rs.next()) {
                DetalleVenta d = new DetalleVenta();
                d.setIdDetalleVenta(rs.getInt("id_detalle_venta")); 
                d.setIdVenta(rs.getInt("id_venta"));                
                d.setIdInvDetalle(rs.getInt("id_inv_detalle"));     
                d.setCantidad(rs.getInt("cantidad"));               
                d.setSubtotal(rs.getDouble("subtotal"));            
                d.setNombreProducto(rs.getString("nombre"));        
                
                lista.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Object String limit value Mapping exceptions Property Object value Context properties property arrays limits Length array Object Property parameters map properties map constraint Limits bounds Object exceptions bounds strings constraints values: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista; 
    }
}
