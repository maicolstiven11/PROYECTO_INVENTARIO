package com.inventario.dao;

import com.inventario.util.Conexion;
import com.inventario.model.Inventario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class InventarioDAO {

     /**
     * Inicia un nuevo inventario para un negocio.
     * También cambia el estado del negocio a 'ACTIVO'.
     * @param idNegocio ID del negocio
     * @param tipoControl Tipo de control (ej: "semanal", "mensual")
     * @param fechaInicio Fecha de inicio seleccionada
     * @return ID del inventario generado, o -1 si falla
     */
    public int iniciarInventario(int idNegocio, String tipoControl, Date fechaInicio) {
        Connection con = null;
        PreparedStatement psInventario = null;
        PreparedStatement psNegocio = null;
        ResultSet rsKeys = null;
        int idGenerado = -1;
        
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // TRANSACCIÓN
            
            // 1. INSERTAR EN INVENTARIO
            String sqlInventario = "INSERT INTO INVENTARIO (id_negocio, fecha_inicio, tipo_control, estado) " +
                                   "VALUES (?, ?, ?, ?)";
            psInventario = con.prepareStatement(sqlInventario, PreparedStatement.RETURN_GENERATED_KEYS);
            psInventario.setInt(1, idNegocio);
            // Usar la fecha pasada como parámetro o la actual si es null
            psInventario.setDate(2, fechaInicio != null ? fechaInicio : new Date(System.currentTimeMillis()));
            psInventario.setString(3, tipoControl != null ? tipoControl : "semanal");
            psInventario.setString(4, "activo");
            
            int filas = psInventario.executeUpdate();
            if (filas > 0) {
                rsKeys = psInventario.getGeneratedKeys();
                if (rsKeys.next()) {
                    idGenerado = rsKeys.getInt(1);
                }
                
                // 2. ACTUALIZAR ESTADO DEL NEGOCIO A 'activo'
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
     * Obtiene el inventario activo de un negocio (si existe)
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
}
