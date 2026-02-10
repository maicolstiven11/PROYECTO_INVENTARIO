package com.inventario.dao;

import com.inventario.model.DetallePedido;
import com.inventario.model.PedidoProveedor;
import com.inventario.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PedidoDAO {

    // Registrar Pedido y sus Detalles (Transacción)
    public boolean registrarPedido(PedidoProveedor pedido, List<DetallePedido> detalles) {
        Connection con = null;
        PreparedStatement psPedido = null;
        PreparedStatement psDetalle = null;
        ResultSet rs = null;
        boolean registrado = false;

        try {
            con = Conexion.getConexion();
            // Desactivar autocommit para manejar transacción
            con.setAutoCommit(false);

            // 1. Insertar PEDIDOS_PROVEEDOR
            String sqlPedido = "INSERT INTO PEDIDOS_PROVEEDOR (fecha_pedido, fecha_entrega, total_pedido, iva_pedido, subtotal, id_inventario, id_proveedor) VALUES (?, ?, ?, ?, ?, ?, ?)";
            psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            psPedido.setDate(1, pedido.getFechaPedido());
            psPedido.setDate(2, pedido.getFechaEntrega());
            psPedido.setDouble(3, pedido.getTotalPedido());
            psPedido.setDouble(4, pedido.getIvaPedido());
            psPedido.setDouble(5, pedido.getSubtotal());
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

            // 2. Insertar DETALLE_PEDIDOS
            String sqlDetalle = "INSERT INTO DETALLE_PEDIDOS (id_pedido_base, id_producto, cantidad_pedida, precio_unitario_real) VALUES (?, ?, ?, ?)";
            psDetalle = con.prepareStatement(sqlDetalle);

            for (DetallePedido det : detalles) {
                psDetalle.setInt(1, idPedidoBase);
                psDetalle.setInt(2, det.getIdProducto());
                psDetalle.setInt(3, det.getCantidadPedida());
                psDetalle.setDouble(4, det.getPrecioUnitarioReal());
                psDetalle.addBatch(); // Agregar al lote
            }

            psDetalle.executeBatch(); // Ejecutar lote

            // Confirmar transacción
            con.commit();
            registrado = true;

        } catch (SQLException e) {
            System.err.println("Error al registrar pedido: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); // Deshacer cambios si hay error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (psPedido != null) psPedido.close();
                if (psDetalle != null) psDetalle.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return registrado;
    }
}
