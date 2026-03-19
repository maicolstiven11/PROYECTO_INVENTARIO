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
     * guarda cada producto que compramos y actualiza el stock sumando lo que compramos.
     */
    public boolean registrarPedido(PedidoProveedor pedido, List<DetallePedido> detalles) {
        Connection con = null; // Conexión
        PreparedStatement psPedido = null; // Para la tabla principal de pedidos (factura)
        PreparedStatement psDetalle = null; // Para los items de cada pedido
        PreparedStatement psStock = null; // Para sumarle al stock lo que acabó de llegar
        ResultSet rs = null; // Para leer la respuesta y el ID autogenerado
        boolean registrado = false; // Bandera para saber si triunfó

        try {
            con = Conexion.getConexion(); // Enlace a la bd
            con.setAutoCommit(false); // IMPORTANTE: Apagamos el guardado automático para que no quede nada a medias si algo falla (Transacciones).

            // 1. Guardamos los datos base del pedido (fecha, entrega, total, iva) en PEDIDOS_PROVEEDOR
            String sqlPedido = "INSERT INTO PEDIDOS_PROVEEDOR (fecha_pedido, fecha_entrega, total_pedido, subtotal, iva_pedido, id_inventario, id_proveedor) VALUES (?, ?, ?, ?, ?, ?, ?)";
            // Le pedimos de nuevo que nos devuelva el ID que le asignó (RETURN_GENERATED_KEYS)
            psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            psPedido.setDate(1, pedido.getFechaPedido()); // Fecha en que se pide
            psPedido.setDate(2, pedido.getFechaEntrega()); // Fecha calculada de entrega
            psPedido.setDouble(3, pedido.getTotalPedido()); // Dinero total a pagarle
            psPedido.setDouble(4, pedido.getSubtotal()); // Dinero antes de IVA
            psPedido.setDouble(5, pedido.getIvaPedido()); // Valor del IVA cobrado
            psPedido.setInt(6, pedido.getIdInventario()); // En qué inventario entra
            psPedido.setInt(7, pedido.getIdProveedor()); // A quién se le compró
            
            int filas = psPedido.executeUpdate(); // Guardamos el pedido principal
            if (filas == 0) { // Si falló al iniciar...
                throw new SQLException("Error: No se pudo registrar el pedido base."); // Forzamos un salto al CATCH de error
            }

            // Atrapamos qué número de venta/pedido se formó recién
            rs = psPedido.getGeneratedKeys();
            int idPedidoBase = 0;
            if (rs.next()) {
                idPedidoBase = rs.getInt(1); // Lo guardamos
            }

            // 2. Preparamos las sentencias para hacer bulto (lotes/batch) y mandar a hacerlas todas de un solo golpe.
            // Para la tabla DETALLE_PEDIDOS (lista del mercado)
            String sqlDetalle = "INSERT INTO DETALLE_PEDIDOS (id_pedido_base, id_inv_detalle, cantidad_pedida, precio_unitario_real) VALUES (?, ?, ?, ?)";
            psDetalle = con.prepareStatement(sqlDetalle);

            // Para la tabla de INVENTARIO_DETALLE para subir nuestro inventario actual
            String sqlSumar = "UPDATE INVENTARIO_DETALLE SET cantidad_inicial = cantidad_inicial + ? WHERE id_detalle = ?";
            psStock = con.prepareStatement(sqlSumar);

            // 3. Recorremos lo que compramos producto por producto
            for (DetallePedido det : detalles) {
                // Rellenamos el formato para el detalle
                psDetalle.setInt(1, idPedidoBase); // Apuntamos a la factura principal
                psDetalle.setInt(2, det.getIdInvDetalle()); // ID del producto de nuestro negocio
                psDetalle.setInt(3, det.getCantidadPedida()); // Cuántos trajimos
                psDetalle.setDouble(4, det.getPrecioUnitarioReal()); // A cuánto nos lo vendió
                psDetalle.addBatch(); // En vez de ejecutar de una, lo añadimos a una "cola de espera" (batch)

                // Rellenamos el formato de la suma del stock
                psStock.setInt(1, det.getCantidadPedida()); // +X de este producto
                psStock.setInt(2, det.getIdInvDetalle()); // A este producto en específico
                psStock.addBatch(); // Añadir a la otra "cola de espera"
            }

            // 4. Se sueltan de golpe y ejecutan todas las órdenes encoladas.
            psDetalle.executeBatch();
            psStock.executeBatch();

            // 5. Tras triunfar en todos los pasos anteriores, hacemos oficial el guardado.
            con.commit();
            registrado = true; // Todo bien
            System.out.println("Pedido " + idPedidoBase + " registrado y stock actualizado con éxito.");

        } catch (SQLException e) { // Si algo falló en cualquier lado (hasta en sumar el stock)
            System.err.println("Error al procesar el pedido: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); // Anular ABSOLUTAMENTE todo para no dejar un pedido a medias pero sin sumar al inventario
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally { // Libera las 4 sentencias
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
        return registrado; // Informar si salió exitoso.
    }

    /**
     * Trae una lista de todos los pedidos (solo las cabeceras/facturas del bulto) realizados
     * en el sistema para ese negocio (independiente de en qué mes fue).
     */
    public List<PedidoProveedor> listarPedidos(int idNegocio) {
        List<PedidoProveedor> lista = new ArrayList<>(); // Almacenaje de resultados
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion(); // Nos enlazamos
            
            // Pide datos principales de la factura (pp.*), pega además DATOS_PROVEEDOR para llevarse su bonito nombre (p.nombre_proveedor)
            // Se asocia también con INVENTARIO para limitar y filtrar de que solo saque facturas que se hicieron en este negocio.
            // Los recientes de primeros (DESC).
            String sql = "SELECT pp.id_pedido_base, pp.fecha_pedido, pp.fecha_entrega, " +
                         "pp.subtotal, pp.iva_pedido, pp.total_pedido, " +
                         "pp.id_proveedor, p.nombre_proveedor " +
                         "FROM PEDIDOS_PROVEEDOR pp " +
                         "INNER JOIN DATOS_PROVEEDOR p ON pp.id_proveedor = p.id_proveedor " +
                         "INNER JOIN INVENTARIO i ON pp.id_inventario = i.id_inventario " +
                         "WHERE i.id_negocio = ? " +
                         "ORDER BY pp.fecha_pedido DESC"; 

            ps = con.prepareStatement(sql); // Manda el script
            ps.setInt(1, idNegocio); // Le enchufa desde qué ID de negocio buscamos
            rs = ps.executeQuery(); // Activa busqueda

            while (rs.next()) { // Recorre renglones resultantes
                PedidoProveedor pedido = new PedidoProveedor(); // Crear la cajita PedidoProveedor
                pedido.setIdPedidoBase(rs.getInt("id_pedido_base")); // Guardamos su cod
                pedido.setFechaPedido(rs.getDate("fecha_pedido")); // Su fecha realizada
                pedido.setFechaEntrega(rs.getDate("fecha_entrega")); // Fecha límite para que llegue el camión
                pedido.setSubtotal(rs.getDouble("subtotal"));  // El dinero que no tiene Iva del global de lo comprado
                pedido.setIvaPedido(rs.getDouble("iva_pedido")); // La carga del IVA de la factura
                pedido.setTotalPedido(rs.getDouble("total_pedido")); // Total
                pedido.setIdProveedor(rs.getInt("id_proveedor")); // El codigo de la empresa vendedora
                pedido.setNombreProveedor(rs.getString("nombre_proveedor")); // El nombre real comercial que se lo trajo con el JOIN arriba 
                lista.add(pedido); // Lo insertamos como nueva celda a nuestra variable Lista para rebotarla a la Web
            }

        } catch (SQLException e) { // Atrapar SQL 
            System.err.println("Error al listar pedidos: " + e.getMessage());
            e.printStackTrace();
        } finally { // Suicidio de recursos
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lista; // Final
    }
}
