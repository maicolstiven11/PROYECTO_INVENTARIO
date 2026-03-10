package com.inventario.dao;

import com.inventario.model.DetallePedido;
import com.inventario.model.PedidoProveedor;
import com.inventario.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    /**
     * Registrar Pedido y sus Detalles (Transacción) + SUMAR STOCK
     * RESTAURADO: Usa subtotal y iva_pedido
     * MANTIENE: Detalle usa id_inv_detalle y suma stock
     */
    public boolean registrarPedido(PedidoProveedor pedido, List<DetallePedido> detalles) {
        Connection con = null;
        PreparedStatement psPedido = null;
        PreparedStatement psDetalle = null;
        PreparedStatement psStock = null;
        ResultSet rs = null;
        boolean registrado = false;

        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // TRANSACCIÓN

            // 1. Insertar PEDIDOS_PROVEEDOR
            // RESTAURADO: subtotal y iva_pedido (columnas originales)
            String sqlPedido = "INSERT INTO PEDIDOS_PROVEEDOR (fecha_pedido, fecha_entrega, total_pedido, subtotal, iva_pedido, id_inventario, id_proveedor) VALUES (?, ?, ?, ?, ?, ?, ?)";
            psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            psPedido.setDate(1, pedido.getFechaPedido());
            psPedido.setDate(2, pedido.getFechaEntrega());
            psPedido.setDouble(3, pedido.getTotalPedido());
            psPedido.setDouble(4, pedido.getSubtotal());    // RESTAURADO
            psPedido.setDouble(5, pedido.getIvaPedido()); // RESTAURADO
            psPedido.setInt(6, pedido.getIdInventario());
            psPedido.setInt(7, pedido.getIdProveedor());

            int filas = psPedido.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se pudo guardar el pedido.");
            }

            // Obtener ID generado
            rs = psPedido.getGeneratedKeys();
            int idPedidoBase = 0;
            if (rs.next()) {
                idPedidoBase = rs.getInt(1);
            }

            // 2. Insertar DETALLE_PEDIDOS + SUMAR STOCK
            // MANTIENE: usa id_inv_detalle
            String sqlDetalle = "INSERT INTO DETALLE_PEDIDOS (id_pedido_base, id_inv_detalle, cantidad_pedida, precio_unitario_real) VALUES (?, ?, ?, ?)";
            psDetalle = con.prepareStatement(sqlDetalle);

            // MANTIENE: SQL para SUMAR stock
            String sqlSumar = "UPDATE INVENTARIO_DETALLE SET cantidad_inicial = cantidad_inicial + ? WHERE id_detalle = ?";
            psStock = con.prepareStatement(sqlSumar);

            for (DetallePedido det : detalles) {
                // Insertar detalle del pedido
                psDetalle.setInt(1, idPedidoBase);
                psDetalle.setInt(2, det.getIdInvDetalle());
                psDetalle.setInt(3, det.getCantidadPedida());
                psDetalle.setDouble(4, det.getPrecioUnitarioReal());
                psDetalle.addBatch();

                // SUMAR stock al inventario
                psStock.setInt(1, det.getCantidadPedida());
                psStock.setInt(2, det.getIdInvDetalle());
                psStock.addBatch();
            }

            psDetalle.executeBatch();
            psStock.executeBatch();

            con.commit();
            registrado = true;
            System.out.println("Pedido registrado con éxito. ID: " + idPedidoBase + " | Stock actualizado.");

        } catch (SQLException e) {
            System.err.println("Error al registrar pedido: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (psPedido != null) psPedido.close();
                if (psDetalle != null) psDetalle.close();
                if (psStock != null) psStock.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return registrado;
    }

    /**
     * Listar pedidos de un negocio
     * RESTAURADO: Lee subtotal y iva_pedido
     */
    public List<PedidoProveedor> listarPedidos(int idNegocio) {
        List<PedidoProveedor> lista = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            // RESTAURADO: subtotal y iva_pedido
            String sql = "SELECT pp.id_pedido_base, pp.fecha_pedido, pp.fecha_entrega, " +
                         "pp.subtotal, pp.iva_pedido, pp.total_pedido, " +
                         "pp.id_proveedor, p.nombre_proveedor " +
                         "FROM PEDIDOS_PROVEEDOR pp " +
                         "INNER JOIN DATOS_PROVEEDOR p ON pp.id_proveedor = p.id_proveedor " +
                         "INNER JOIN INVENTARIO i ON pp.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ? " +
                         "ORDER BY pp.fecha_pedido DESC";

            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio);
            rs = ps.executeQuery();

            while (rs.next()) {
                PedidoProveedor pedido = new PedidoProveedor();
                pedido.setIdPedidoBase(rs.getInt("id_pedido_base"));
                pedido.setFechaPedido(rs.getDate("fecha_pedido"));
                pedido.setFechaEntrega(rs.getDate("fecha_entrega"));
                pedido.setSubtotal(rs.getDouble("subtotal")); // RESTAURADO
                pedido.setIvaPedido(rs.getDouble("iva_pedido")); // RESTAURADO
                pedido.setTotalPedido(rs.getDouble("total_pedido"));
                pedido.setIdProveedor(rs.getInt("id_proveedor"));
                pedido.setNombreProveedor(rs.getString("nombre_proveedor"));
                lista.add(pedido);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar pedidos: " + e.getMessage());
            e.printStackTrace();
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
}
