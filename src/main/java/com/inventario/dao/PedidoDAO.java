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
 * Clase PedidoDAO.
 *
 * Módulo encargado de guardar en la base de datos las compras o pedidos
 * que se le hacen a un proveedor, incluyendo todos los productos detalle.
 */
public class PedidoDAO {

    /**
     * Guarda un pedido completo junto con su lista de productos pedida.
     * Esta función hace varias cosas a la vez: guarda la cabecera del pedido,
     * guarda cada producto que compramos y actualiza el stock sumando lo que
     * compramos.
     */
    /**
     * Guarda un pedido completo junto con su lista de productos pedida.
     * Esta función hace varias cosas a la vez: guarda la cabecera del pedido,
     * guarda cada producto que compramos y actualiza el stock sumando lo que
     * compramos.
     */
    public boolean registrarPedido(PedidoProveedor pedido, List<DetallePedido> detalles) {
        Connection con = null; // Socket de conexión física
        PreparedStatement psPedido = null; // Comando para la cabecera (factura de pedido)
        PreparedStatement psDetalle = null; // Comando para la lista de productos comprados
        PreparedStatement psStock = null; // Comando para inyectar stock al inventario
        ResultSet rs = null; // Cofre de llaves autogeneradas
        boolean registrado = false; // Variable de éxito

        try {
            con = Conexion.getConexion(); // Conectamos

            // TRANSACCIÓN: Apagamos el AutoCommit.
            // Esto es VITAL porque si falla el registro de productos pero se guardó la
            // factura,
            // el inventario quedaría descuadrado. Todo se guarda junto al final o nada.
            con.setAutoCommit(false);

            // PASO 1: Insertar la información global de la compra.
            String sqlPedido = "INSERT INTO PEDIDOS_PROVEEDOR (fecha_pedido, fecha_entrega, total_pedido, subtotal, iva_pedido, id_inventario, id_proveedor) VALUES (?, ?, ?, ?, ?, ?, ?)";

            psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            psPedido.setDate(1, pedido.getFechaPedido()); // Fecha de la orden
            psPedido.setDate(2, pedido.getFechaEntrega()); // Fecha de llegada
            psPedido.setDouble(3, pedido.getTotalPedido()); // Valor final facturado
            psPedido.setDouble(4, pedido.getSubtotal()); // Valor neto
            psPedido.setDouble(5, pedido.getIvaPedido()); // Impuesto
            psPedido.setInt(6, pedido.getIdInventario()); // Mes/Periodo contable
            psPedido.setInt(7, pedido.getIdProveedor()); // Quién nos vendió

            int filas = psPedido.executeUpdate(); // Insertamos la cabecera
            if (filas == 0) {
                throw new SQLException("Error: No se pudo registrar el pedido base.");
            }

            // ATRAMOS EL ID: Necesitamos el número de esta factura para marcar los
            // productos abajo.
            rs = psPedido.getGeneratedKeys();
            int idPedidoBase = 0;
            if (rs.next()) {
                idPedidoBase = rs.getInt(1); // Este es el ID de la factura de compra
            }

            // PASO 2: Preparar inserción masiva (BATCH).
            // Para la lista de ítems comprados:
            String sqlDetalle = "INSERT INTO DETALLE_PEDIDOS (id_pedido_base, id_inv_detalle, cantidad_pedida, precio_unitario_real) VALUES (?, ?, ?, ?)";
            psDetalle = con.prepareStatement(sqlDetalle);

            // Para sumar el stock al inventario activo (cantidad_inicial):
            String sqlSumar = "UPDATE INVENTARIO_DETALLE SET cantidad_inicial = cantidad_inicial + ? WHERE id_detalle = ?";
            psStock = con.prepareStatement(sqlSumar);

            // PASO 3: Encolar órdenes (addBatch).
            for (DetallePedido det : detalles) {
                // Rellenamos datos del ítem:
                psDetalle.setInt(1, idPedidoBase); // Apuntamos a la factura de arriba
                psDetalle.setInt(2, det.getIdInvDetalle()); // ID del producto en bodega
                psDetalle.setInt(3, det.getCantidadPedida()); // Cuánto llegó
                psDetalle.setDouble(4, det.getPrecioUnitarioReal()); // A cuánto nos lo vendieron hoy
                psDetalle.addBatch(); // Lo guardamos en una "lista de espera"

                // Rellenamos datos de actualización de stock:
                psStock.setInt(1, det.getCantidadPedida()); // Sumamos lo que llegó
                psStock.setInt(2, det.getIdInvDetalle()); // Al producto que llegó
                psStock.addBatch(); // Lista de espera para actualización
            }

            // PASO 4: Ejecutar todas las órdenes de una sola vez para máxima eficiencia.
            psDetalle.executeBatch();
            psStock.executeBatch();

            // FINALIZACIÓN: Si llegamos aquí sin errores, guardamos todo permanentemente.
            con.commit();
            registrado = true;
            System.out.println("Pedido " + idPedidoBase + " registrado y stock actualizado con éxito.");

        } catch (SQLException e) {
            System.err.println("Error al procesar el pedido: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); // DESHACER TODO: Si algo falló, borramos el rastro del pedido para evitar
                                    // descuadres.
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                // Liberación de todos los cables y preparadores ocupados
                if (rs != null)
                    rs.close();
                if (psPedido != null)
                    psPedido.close();
                if (psDetalle != null)
                    psDetalle.close();
                if (psStock != null)
                    psStock.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return registrado; // Informamos si la compra se procesó.
    }

    /**
     * Trae una lista de todos los pedidos (solo las cabeceras/facturas del bulto)
     * realizados
     * en el sistema para ese negocio (independiente de en qué mes fue).
     */
    public List<PedidoProveedor> listarPedidos(int idNegocio) {
        List<PedidoProveedor> lista = new ArrayList<>(); // Contenedor de facturas
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion(); // Nos enlazamos

            String sql = "SELECT pp.id_pedido_base, pp.fecha_pedido, pp.fecha_entrega, " + // Selecciona columnas de la
                                                                                           // tabla PEDIDOS_PROVEEDOR:
                                                                                           // id del pedido, fecha de
                                                                                           // pedido y fecha de entrega

                    "pp.subtotal, pp.iva_pedido, pp.total_pedido, " + // También trae los valores monetarios: subtotal,
                                                                      // IVA y total del pedido

                    "pp.id_proveedor, p.nombre_proveedor " + // Incluye el id del proveedor y su nombre (desde
                                                             // DATOS_PROVEEDOR)

                    "FROM PEDIDOS_PROVEEDOR pp " + // Tabla principal: PEDIDOS_PROVEEDOR con alias "pp"

                    "INNER JOIN DATOS_PROVEEDOR p ON pp.id_proveedor = p.id_proveedor " + // Une con DATOS_PROVEEDOR
                                                                                          // para obtener el nombre del
                                                                                          // proveedor, solo si existe
                                                                                          // coincidencia en
                                                                                          // id_proveedor

                    "INNER JOIN INVENTARIO i ON pp.id_inventario = i.id_inventario " + // Une con INVENTARIO para
                                                                                       // relacionar el pedido con un
                                                                                       // inventario específico

                    "WHERE i.id_negocio = ? " + // Filtra: solo pedidos asociados al negocio indicado (parámetro ?)

                    "ORDER BY pp.fecha_pedido DESC"; // Ordena los resultados por fecha de pedido, de más reciente a más
                                                     // antiguo

            ps = con.prepareStatement(sql);
            ps.setInt(1, idNegocio);
            rs = ps.executeQuery();

            while (rs.next()) { // Recorremos facturas encontradas
                PedidoProveedor pedido = new PedidoProveedor();
                // Llenamos el objeto Java con los datos de las columnas de MySQL:
                pedido.setIdPedidoBase(rs.getInt("id_pedido_base"));
                pedido.setFechaPedido(rs.getDate("fecha_pedido"));
                pedido.setFechaEntrega(rs.getDate("fecha_entrega"));
                pedido.setSubtotal(rs.getDouble("subtotal"));
                pedido.setIvaPedido(rs.getDouble("iva_pedido"));
                pedido.setTotalPedido(rs.getDouble("total_pedido"));
                pedido.setIdProveedor(rs.getInt("id_proveedor"));
                pedido.setNombreProveedor(rs.getString("nombre_proveedor")); // Obtenido por el JOIN
                lista.add(pedido); // Lo añadimos a la lista para la web
            }
        } catch (SQLException e) {
            System.err.println("Error al listar pedidos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lista; // Enviamos el historial de compras
    }
}
