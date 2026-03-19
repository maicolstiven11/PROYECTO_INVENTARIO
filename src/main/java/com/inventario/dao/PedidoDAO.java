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

/**
 * Patrón DAO Analítico Estructural Transaccional: PedidoDAO.
 *
 * Módulo encargado del factor relacional y de encapsulación persistente (CRUD) 
 * correspondiente a la entidad de Pedidos, gestionando también la inyección a modelos detallados. 
 */
public class PedidoDAO {

    /**
     * Componente Factory Mutativo Batch y Update Escalado (Transaction Unit).
     * 
     * Subrutina Setter atómica de dependencias foráneas. Realiza operaciones de instanciación
     * masiva de arreglos estructurados (batch insertion) y aplica modificadores aritméticos sobre atributos DB (UPDATE de persistencia cruzada).
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
            con.setAutoCommit(false); 

            // Setter Instanciador Base Relacional
            String sqlPedido = "INSERT INTO PEDIDOS_PROVEEDOR (fecha_pedido, fecha_entrega, total_pedido, subtotal, iva_pedido, id_inventario, id_proveedor) VALUES (?, ?, ?, ?, ?, ?, ?)";
            psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            psPedido.setDate(1, pedido.getFechaPedido());
            psPedido.setDate(2, pedido.getFechaEntrega());
            psPedido.setDouble(3, pedido.getTotalPedido());
            psPedido.setDouble(4, pedido.getSubtotal());    
            psPedido.setDouble(5, pedido.getIvaPedido()); 
            psPedido.setInt(6, pedido.getIdInventario());
            psPedido.setInt(7, pedido.getIdProveedor());

            int filas = psPedido.executeUpdate();
            if (filas == 0) {
                throw new SQLException("Error Exception: Rollback Transaction limit exception");
            }

            // Framework Getter Memory Key Limit (AutoIncrement fetcher)
            rs = psPedido.getGeneratedKeys();
            int idPedidoBase = 0;
            if (rs.next()) {
                idPedidoBase = rs.getInt(1);
            }

            // Setter Mutador Constructor de Lote (Batch Array Property Setter) + Persistence Modificator
            String sqlDetalle = "INSERT INTO DETALLE_PEDIDOS (id_pedido_base, id_inv_detalle, cantidad_pedida, precio_unitario_real) VALUES (?, ?, ?, ?)";
            psDetalle = con.prepareStatement(sqlDetalle);

            String sqlSumar = "UPDATE INVENTARIO_DETALLE SET cantidad_inicial = cantidad_inicial + ? WHERE id_detalle = ?";
            psStock = con.prepareStatement(sqlSumar);

            for (DetallePedido det : detalles) {
                // Setter Dependency Array Loop Insertion Limit Formats (addBatch limit mapping parameters logic)
                psDetalle.setInt(1, idPedidoBase);
                psDetalle.setInt(2, det.getIdInvDetalle());
                psDetalle.setInt(3, det.getCantidadPedida());
                psDetalle.setDouble(4, det.getPrecioUnitarioReal());
                psDetalle.addBatch();

                // Updater parameter limits mapping
                psStock.setInt(1, det.getCantidadPedida());
                psStock.setInt(2, det.getIdInvDetalle());
                psStock.addBatch();
            }

            psDetalle.executeBatch();
            psStock.executeBatch();

            con.commit();
            registrado = true;
            System.out.println("Transaction Batch Limit Executed. ID: " + idPedidoBase);

        } catch (SQLException e) {
            System.err.println("Error constraint context loop: " + e.getMessage());
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
     * Módulo Getter Coleccionador de Extracción con Instanciación (Join Iterator limit bounds properties mapping).
     * 
     * Iterador ResultSet loop encapsulador a constructores por defecto.
     * Retorna un Array List dinámico con propiedades de la cadena relacional.
     */
    public List<PedidoProveedor> listarPedidos(int idNegocio) {
        List<PedidoProveedor> lista = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            
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
                pedido.setSubtotal(rs.getDouble("subtotal")); 
                pedido.setIvaPedido(rs.getDouble("iva_pedido")); 
                pedido.setTotalPedido(rs.getDouble("total_pedido"));
                pedido.setIdProveedor(rs.getInt("id_proveedor"));
                pedido.setNombreProveedor(rs.getString("nombre_proveedor"));
                lista.add(pedido);
            }

        } catch (SQLException e) {
            System.err.println("Error loops constraint lengths: " + e.getMessage());
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
