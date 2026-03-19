package com.inventario.dao;

import com.inventario.util.Conexion;      
import com.inventario.model.Inventario;   
import java.sql.Connection;               
import java.sql.PreparedStatement;        
import java.sql.ResultSet;               
import java.sql.SQLException;            
import java.sql.Date;                    

/**
 * Clase InventarioDAO (Data Access Object).
 * 
 * Controlador de Capa de Persistencia y Factoría Generatriz de Consultas SQL (DML y DQL).
 * Administra el ciclo de vida, estado y mutabilidad de la entidad relacional INVENTARIO.
 * Implementa el patrón estructural DAO para abstraer la interacción bidireccional entre los objetos
 * de la JVM y la base de datos relacional.
 */
public class InventarioDAO {

    /**
     * Módulo Factory Mutativo Atómico (Transaccional).
     * 
     * Constructor relacional que inserta una entidad Inventario inicializando
     * sus propiedades. Funciona como un Setter compuesto que utiliza un patrón 
     * Unit of Work mediante la instrucción rollback en caso de fallos de persistencia.
     */
    public int iniciarInventario(int idNegocio, String tipoControl, Date fechaInicio) {
        Connection con = null;
        PreparedStatement psInventario = null;  
        PreparedStatement psNegocio = null;     
        ResultSet rsKeys = null;                
        int idGenerado = -1;                    
        
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); 
            
            String sqlInventario = "INSERT INTO INVENTARIO (id_negocio, tipo_control, estado, fecha_inicio) " +
                                   "VALUES (?, ?, ?, ?)";
            psInventario = con.prepareStatement(sqlInventario, PreparedStatement.RETURN_GENERATED_KEYS);
            psInventario.setInt(1, idNegocio);                                         
            psInventario.setString(2, tipoControl != null ? tipoControl : "mensual"); 
            psInventario.setString(3, "activo");                                       
            psInventario.setDate(4, fechaInicio);                                      
            
            int filas = psInventario.executeUpdate(); 
            if (filas > 0) {
                rsKeys = psInventario.getGeneratedKeys(); 
                if (rsKeys.next()) {
                    idGenerado = rsKeys.getInt(1); 
                }
                
                String sqlNegocio = "UPDATE NEGOCIO SET estado = 'activo' WHERE id_negocio = ?";
                psNegocio = con.prepareStatement(sqlNegocio);
                psNegocio.setInt(1, idNegocio); 
                psNegocio.executeUpdate();
                
                con.commit(); 
                System.out.println("DAO: Inventario iniciado con ID: " + idGenerado + " para Negocio: " + idNegocio);
            } else {
                con.rollback(); 
            }
            
        } catch (SQLException e) {
            System.err.println("Error al iniciar inventario: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
                if (rsKeys != null) rsKeys.close();
                if (psNegocio != null) psNegocio.close();
                if (psInventario != null) psInventario.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return idGenerado; 
    }
    
    /**
     * Módulo Getter de Instancia Singular.
     * 
     * Algoritmo de extracción (DQL) configurado con una restricción WHERE ('activo').
     * Retorna un Objeto del Modelo instanciado e inicializado a través de Setters de propiedades.
     */
    public Inventario obtenerInventarioActivo(int idNegocio) {
        Inventario inv = null;  
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            
            String sql = "SELECT * FROM INVENTARIO WHERE id_negocio = ? AND estado = 'activo'";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio); 
            rs = ps.executeQuery();
            
            if (rs.next()) { 
                inv = new Inventario();
                inv.setIdInventario(rs.getInt("id_inventario")); 
                inv.setIdNegocio(rs.getInt("id_negocio"));       
                inv.setFechaInicio(rs.getDate("fecha_inicio"));  
                inv.setTipoControl(rs.getString("tipo_control"));
                inv.setEstado(rs.getString("estado"));           
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener inventario activo: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return inv; 
    }

    /**
     * Algoritmo Coleccionador (Getter de Estructura de Datos List).
     * 
     * Iterador sobre ResultSet de persistencia para poblar una clase Collection.
     * Constructor generatriz múltiple que retorna un arreglo dinámico de objetos Inventario.
     */
    public java.util.List<Inventario> listarInventariosPorNegocio(int idNegocio) {
        java.util.List<Inventario> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            
            String sql = "SELECT * FROM INVENTARIO WHERE id_negocio = ? ORDER BY fecha_inicio DESC";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio); 
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Inventario inv = new Inventario();
                inv.setIdInventario(rs.getInt("id_inventario")); 
                inv.setIdNegocio(rs.getInt("id_negocio"));
                inv.setFechaInicio(rs.getDate("fecha_inicio"));  
                inv.setTipoControl(rs.getString("tipo_control"));
                inv.setEstado(rs.getString("estado"));           
                lista.add(inv);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar inventarios: " + e.getMessage());
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
     * Setter Mutacional (Modificador de Propiedad Relacional).
     * 
     * Abstracción transaccional unitaria (UPDATE) orientada exclusivamente a la manipulación
     * del atributo estado mediante inyección controlada de dependencias (PreparedStatement).
     */
    public boolean finalizarInventario(int idInventario) {
        Connection con = null;
        PreparedStatement psEnv = null;
        boolean finalizado = false;
        
        try {
            con = Conexion.getConexion();
            
            String sqlInv = "UPDATE INVENTARIO SET estado = 'inactivo' WHERE id_inventario = ?";
            psEnv = con.prepareStatement(sqlInv);
            psEnv.setInt(1, idInventario); 
            int f1 = psEnv.executeUpdate();
            
            if (f1 > 0) { 
                finalizado = true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al finalizar inventario: " + e.getMessage());
        } finally {
            try {
                if (psEnv != null) psEnv.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return finalizado; 
    }

    /**
     * Módulo Fetch Extractor condicional (Getter de límite estricto).
     * 
     * Recupera el objeto Inventario superior del stack relacional inactivo
     * mediante la directiva ORDER BY limitante. Instancia un encapsulador POJO y retorna
     * la ubicación de memoria del objeto (o valor Nullable).
     */
    public Inventario obtenerUltimoInventarioCerrado(int idNegocio) {
        Inventario inv = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            String sql = "SELECT * FROM INVENTARIO WHERE id_negocio = ? AND estado = 'inactivo' ORDER BY id_inventario DESC LIMIT 1";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                inv = new Inventario();
                inv.setIdInventario(rs.getInt("id_inventario"));
                inv.setIdNegocio(rs.getInt("id_negocio"));
                inv.setFechaInicio(rs.getDate("fecha_inicio"));
                inv.setTipoControl(rs.getString("tipo_control"));
                inv.setEstado(rs.getString("estado"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener último inventario cerrado: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return inv;
    }
}
