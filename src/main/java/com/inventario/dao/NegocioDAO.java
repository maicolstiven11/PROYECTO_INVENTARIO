package com.inventario.dao;

import com.inventario.util.Conexion;   
import com.inventario.model.Negocio;   
import java.sql.Connection;            
import java.sql.PreparedStatement;     
import java.sql.SQLException;         
import java.sql.ResultSet;            
import java.util.ArrayList;           
import java.util.List;                

/**
 * Patrón Estructural Data Access Object (DAO): NegocioDAO.
 * 
 * Clase contenedora de subrutinas transaccionales (Setters Inserters, Getters Mappers, Mutators Delete).
 * Regula la persistencia relacional multidireccional asociada al POJO Negocio.
 */
public class NegocioDAO {

    /**
     * Módulo Factory Mutacional Atómico Multinodal.
     * 
     * Constructor transaccional que persiste (INSERT) entidades instanciadas en la base de datos central 
     * y genera dependencias foráneas (Tabla Puente). Implementa autoCommit desactivado conformando Unit of Work.
     */
    public int registrarNegocio(Negocio negocio, int idUsuario) {
        Connection con = null;
        PreparedStatement psNegocio = null;  
        PreparedStatement psVinculo = null;  
        ResultSet rsKeys = null;             
        int idGenerado = -1;                 
        
        try {
            con = Conexion.getConexion();     
            con.setAutoCommit(false);          
            
            String sqlNegocio = "INSERT INTO NEGOCIO (nombre, direccion, estado) VALUES (?, ?, ?)";
            psNegocio = con.prepareStatement(sqlNegocio, PreparedStatement.RETURN_GENERATED_KEYS); 
            psNegocio.setString(1, negocio.getNombre());    
            psNegocio.setString(2, negocio.getDireccion()); 
            psNegocio.setString(3, "inactivo");              
            
            int filas = psNegocio.executeUpdate(); 
            if (filas > 0) {
                rsKeys = psNegocio.getGeneratedKeys(); 
                if (rsKeys.next()) {
                    idGenerado = rsKeys.getInt(1); 
                    negocio.setIdNegocio(idGenerado); 
                }
                
                if (idUsuario > 0 && idGenerado > 0) {
                    String sqlVinculo = "INSERT INTO USUARIO_NEGOCIO (id_usuario, id_negocio) VALUES (?, ?)";
                    psVinculo = con.prepareStatement(sqlVinculo);
                    psVinculo.setInt(1, idUsuario);   
                    psVinculo.setInt(2, idGenerado);  
                    psVinculo.executeUpdate();
                    System.out.println("DAO: Negocio " + idGenerado + " vinculado con Usuario " + idUsuario);
                }
                
                con.commit(); 
                System.out.println("DAO: Negocio registrado con ID: " + idGenerado);
            } else {
                con.rollback(); 
            }
            
        } catch (SQLException e) {
            System.out.println("Error al registrar negocio: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } 
            }
            throw new RuntimeException("ErrorSQL: " + e.getMessage());
        } finally {
            try {
                if (con != null) con.setAutoCommit(true); 
                if (rsKeys != null) rsKeys.close();
                if (psVinculo != null) psVinculo.close();
                if (psNegocio != null) psNegocio.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return idGenerado; 
    }

    /**
     * Módulo Getter de Instanciación de Colecciones Estructuradas.
     * 
     * Iterador de persistencia subquery-bound.
     * Genera instancias POJO Negocio y muta internamente variables booleanas 
     * en memoria basándose en contadores SQL internos mapeados lógicamente (tiene_inv).
     */
    public List<Negocio> listarNegocios(int idUsuario) {
        List<Negocio> lista = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            
            String sql = "SELECT n.*, " +
                         "(SELECT COUNT(*) FROM INVENTARIO i WHERE i.id_negocio = n.id_negocio AND i.estado = 'activo') as tiene_inv " +
                         "FROM NEGOCIO n " +
                         "INNER JOIN USUARIO_NEGOCIO un ON n.id_negocio = un.id_negocio " + 
                         "WHERE un.id_usuario = ?"; 
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario); 
            rs = ps.executeQuery();
            
            while(rs.next()){
                Negocio n = new Negocio();
                n.setIdNegocio(rs.getInt("id_negocio"));       
                n.setNombre(rs.getString("nombre"));           
                n.setDireccion(rs.getString("direccion"));     
                n.setEstado(rs.getString("estado"));           
                
                boolean activo = rs.getInt("tiene_inv") > 0;   
                n.setTieneInventarioActivo(activo);            
                
                lista.add(n);
            }
        } catch (SQLException e) {
            System.err.println("Error listar negocios: " + e.getMessage());
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
     * Controlador Mutador en Cascada Strict (Destructor Transaccional).
     * 
     * Rutina DELETE escalonada (cascade limits constraint handler) que ejecuta
     * una eliminación de la cadena Entity-Relationship de arriba hacia abajo
     * encapsulado bajo Unit of Work (rollback exception bounds).
     */
    public boolean eliminarNegocio(int idNegocio) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;
        
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); 
            
            String sql1 = "DELETE dv FROM DETALLE_VENTA dv " +
                         "INNER JOIN VENTA v ON dv.id_venta = v.id_venta " +         
                         "INNER JOIN INVENTARIO i ON v.id_inventario = i.id_inventario " + 
                         "WHERE i.id_negocio = ?";                                    
            ps = con.prepareStatement(sql1);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            String sql2 = "DELETE v FROM VENTA v " +
                         "INNER JOIN INVENTARIO i ON v.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql2);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            String sql3_1 = "DELETE dp FROM DETALLE_PEDIDOS dp " +
                            "INNER JOIN PEDIDOS_PROVEEDOR pp ON dp.id_pedido_base = pp.id_pedido_base " + 
                            "INNER JOIN INVENTARIO i ON pp.id_inventario = i.id_inventario " +            
                            "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql3_1);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            String sql3_2 = "DELETE pp FROM PEDIDOS_PROVEEDOR pp " +
                            "INNER JOIN INVENTARIO i ON pp.id_inventario = i.id_inventario " +
                            "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql3_2);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            String sql3 = "DELETE g FROM GASTO_DIARIO g " +
                         "INNER JOIN INVENTARIO i ON g.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql3);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            String sql4 = "DELETE di FROM INVENTARIO_DETALLE di " +
                         "INNER JOIN INVENTARIO i ON di.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql4);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            String sql5 = "DELETE FROM INVENTARIO WHERE id_negocio = ?";
            ps = con.prepareStatement(sql5);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            String sql6 = "DELETE FROM USUARIO_NEGOCIO WHERE id_negocio = ?";
            ps = con.prepareStatement(sql6);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            String sql7 = "DELETE FROM NEGOCIO WHERE id_negocio = ?";
            ps = con.prepareStatement(sql7);
            ps.setInt(1, idNegocio);
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                eliminado = true;
                con.commit(); 
                System.out.println("Negocio " + idNegocio + " eliminado con todos sus datos.");
            } else {
                con.rollback(); 
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar negocio: " + e.getMessage());
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
     * Módulo Factory Consultor Iterador Escalar.
     * 
     * Accionador Abstracto tipo Getter matemático. Ejecuta una subrutina tipo Count()
     * retornando atributos numéricos encapsulados dentro de la variable primitiva.
     */
    public int contarNegocios(int idUsuario) {
        int cantidad = 0;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            String sql = "SELECT COUNT(*) FROM NEGOCIO n " +
                         "INNER JOIN USUARIO_NEGOCIO un ON n.id_negocio = un.id_negocio " +
                         "WHERE un.id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario); 
            rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getInt(1); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return cantidad; 
    }

}
