package com.inventario.dao;

import com.inventario.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DetalleInventarioDAO {

    /**
     * Registra el stock inicial de un producto en un inventario específico.
     * @param idInventario ID del inventario activo
     * @param idProducto ID del producto
     * @param cantidadInicial Cantidad al inicio del periodo
     * @return true si se guardó correctamente
     */
    public boolean insertarDetalle(int idInventario, int idProducto, double cantidadInicial) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean registrado = false;
        
        try {
            con = Conexion.getConexion();
            String sql = "INSERT INTO INVENTARIO_DETALLE (id_inventario, id_producto, cantidad_inicial) VALUES (?, ?, ?)";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idInventario);
            ps.setInt(2, idProducto);
            ps.setDouble(3, cantidadInicial);
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                registrado = true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al insertar detalle inventario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return registrado;
    }
}
