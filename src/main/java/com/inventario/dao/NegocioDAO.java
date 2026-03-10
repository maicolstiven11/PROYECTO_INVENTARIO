package com.inventario.dao;

import com.inventario.util.Conexion;
import com.inventario.model.Negocio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NegocioDAO {

    // REGISTRAR NEGOCIO Y VINCULAR CON USUARIO
    // Devuelve el ID generado (o -1 si falla)
    public int registrarNegocio(Negocio negocio, int idUsuario) {
        Connection con = null;
        PreparedStatement psNegocio = null;
        PreparedStatement psVinculo = null;
        ResultSet rsKeys = null;
        int idGenerado = -1;
        
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // TRANSACCIÓN
            
            // 1. INSERTAR EN NEGOCIO
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
                
                // 2. INSERTAR EN USUARIO_NEGOCIO (Vincular usuario con negocio)
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

    // LISTAR NEGOCIOS DEL USUARIO ACTUAL
    public List<Negocio> listarNegocios(int idUsuario) {
        List<Negocio> lista = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = Conexion.getConexion();
            // JOIN para obtener solo los negocios del usuario
            // LEFT JOIN para saber si tiene inventario activo
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
                
                // Si el conteo es > 0, tiene inventario activo
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


    // ELIMINAR NEGOCIO (Cascada: elimina todo lo relacionado)
    public boolean eliminarNegocio(int idNegocio) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;
        
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false);
            
            // 1. Eliminar DETALLE_VENTA (depende de VENTA que depende de INVENTARIO)
            String sql1 = "DELETE dv FROM DETALLE_VENTA dv " +
                         "INNER JOIN VENTA v ON dv.id_venta = v.id_venta " +
                         "INNER JOIN INVENTARIO i ON v.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql1);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // 2. Eliminar VENTA (depende de INVENTARIO)
            String sql2 = "DELETE v FROM VENTA v " +
                         "INNER JOIN INVENTARIO i ON v.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql2);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // 3. Eliminar DETALLE_PEDIDOS (depende de PEDIDOS_PROVEEDOR que depende de INVENTARIO)
            String sql3_1 = "DELETE dp FROM DETALLE_PEDIDOS dp " +
                            "INNER JOIN PEDIDOS_PROVEEDOR pp ON dp.id_pedido_base = pp.id_pedido_base " +
                            "INNER JOIN INVENTARIO i ON pp.id_inventario = i.id_inventario " +
                            "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql3_1);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // 4. Eliminar PEDIDOS_PROVEEDOR (depende de INVENTARIO)
            String sql3_2 = "DELETE pp FROM PEDIDOS_PROVEEDOR pp " +
                            "INNER JOIN INVENTARIO i ON pp.id_inventario = i.id_inventario " +
                            "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql3_2);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // 5. Eliminar GASTO_DIARIO (depende de INVENTARIO)
            String sql3 = "DELETE g FROM GASTO_DIARIO g " +
                         "INNER JOIN INVENTARIO i ON g.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql3);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // 6. Eliminar DETALLE_INVENTARIO (nombre correcto: INVENTARIO_DETALLE)
            String sql4 = "DELETE di FROM INVENTARIO_DETALLE di " +
                         "INNER JOIN INVENTARIO i ON di.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ?";
            ps = con.prepareStatement(sql4);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // 7. Eliminar INVENTARIO
            String sql5 = "DELETE FROM INVENTARIO WHERE id_negocio = ?";
            ps = con.prepareStatement(sql5);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // 8. Eliminar vínculo USUARIO_NEGOCIO
            String sql6 = "DELETE FROM USUARIO_NEGOCIO WHERE id_negocio = ?";
            ps = con.prepareStatement(sql6);
            ps.setInt(1, idNegocio);
            ps.executeUpdate();
            ps.close();
            
            // 9. Finalmente, eliminar el NEGOCIO
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

    // CONTAR NEGOCIOS DEL USUARIO (Para estadísticas)
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

    // MÉTODO MAIN PARA PROBAR INDEPENDIENTE

}
