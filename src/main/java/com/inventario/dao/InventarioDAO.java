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
     * RESTAURADO: Se vuelve a guardar fecha_inicio
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
            
            // 1. INSERTAR EN INVENTARIO (con fecha_inicio RESTAURADA)
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
     * RESTAURADO: Lee fecha_inicio
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
                inv.setFechaInicio(rs.getDate("fecha_inicio")); // RESTAURADO
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
     * Lista todos los inventarios de un negocio (activos y finalizados).
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
     * Finaliza un inventario activo y cambia el estado del negocio a 'inactivo'.
     */
    public boolean finalizarInventario(int idInventario) {
        Connection con = null;
        PreparedStatement psEnv = null;
        PreparedStatement psNeg = null;
        boolean finalizado = false;
        
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false);
            
            // 1. Finalizar inventario
            String sqlInv = "UPDATE INVENTARIO SET estado = 'finalizado', fecha_fin = CURDATE() WHERE id_inventario = ?";
            psEnv = con.prepareStatement(sqlInv);
            psEnv.setInt(1, idInventario);
            int f1 = psEnv.executeUpdate();
            
            // 2. Cambiar estado del negocio a 'inactivo'
            String sqlNeg = "UPDATE NEGOCIO SET estado = 'inactivo' WHERE id_negocio = " +
                            "(SELECT id_negocio FROM INVENTARIO WHERE id_inventario = ?)";
            psNeg = con.prepareStatement(sqlNeg);
            psNeg.setInt(1, idInventario);
            int f2 = psNeg.executeUpdate();
            
            if (f1 > 0 && f2 > 0) {
                con.commit();
                finalizado = true;
            } else {
                con.rollback();
            }
            
        } catch (SQLException e) {
            System.err.println("Error al finalizar inventario: " + e.getMessage());
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
                if (psEnv != null) psEnv.close();
                if (psNeg != null) psNeg.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return finalizado;
    }
}
