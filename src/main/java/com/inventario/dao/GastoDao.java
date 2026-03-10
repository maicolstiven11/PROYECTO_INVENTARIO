package com.inventario.dao;

import com.inventario.model.Gasto;
import com.inventario.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GastoDao {
    /**
     * Registra un nuevo gasto.
     * RESTAURADO: Vuelve a usar id_inventario.
     */
    public boolean registrarGasto(Gasto g) throws SQLException{
        Connection con = null;
        PreparedStatement ps = null;
        boolean registrado = false;
        
        try{
            con = Conexion.getConexion();
            // RESTAURADO: id_inventario (en vez de id_negocio) y quitado direccion
            String sql = "INSERT INTO GASTO_DIARIO (id_inventario, cantidad, fecha, subtotal, descripcion) VALUES (?,?,?,?,?)";
            
            ps = con.prepareStatement(sql);
            
            ps.setInt(1, g.getId_inventario());
            ps.setInt(2, g.getCantidad());
            ps.setDate(3, g.getFecha());
            ps.setDouble(4, g.getSubtotal());
            ps.setString(5, g.getDescripcion());
            
            if(ps.executeUpdate()>0){
                registrado = true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            
            try {
                if (ps != null) ps.close(); 
                if (con != null) con.close(); 
            } catch (SQLException ex) {
                ex.printStackTrace(); 
            }
        }
    
        return registrado;
    }

    /**
     * Listar gastos por negocio.
     * RESTAURADO: JOIN con INVENTARIO para filtrar por negocio.
     */
    public java.util.List<Gasto> listarGastos(int idNegocio) {
        java.util.List<Gasto> lista = new java.util.ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // RESTAURADO: JOIN con INVENTARIO necesario para filtrar por id_negocio
            String sql = "SELECT g.* FROM GASTO_DIARIO g " +
                         "INNER JOIN INVENTARIO i ON g.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ? " +
                         "ORDER BY g.fecha DESC";
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Gasto g = new Gasto();
                g.setId_gastos(rs.getInt("id_gastos"));
                g.setId_inventario(rs.getInt("id_inventario"));
                g.setCantidad(rs.getInt("cantidad"));
                g.setFecha(rs.getDate("fecha"));
                g.setSubtotal(rs.getDouble("subtotal"));
                g.setDescripcion(rs.getString("descripcion"));
                
                lista.add(g);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Gastos: " + e.getMessage());
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
