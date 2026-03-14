package com.inventario.model;

import java.sql.Date;       // Importación para manejar fechas de pedido/entrega desde MySQL
import java.util.List;       // Importación para la lista de detalles del pedido

/**
 * MODELO: Clase PedidoProveedor (Entidad/POJO)
 * 
 * Representa la tabla PEDIDOS_PROVEEDOR de la base de datos MySQL.
 * Un PedidoProveedor es una orden de compra a un proveedor dentro de un inventario activo.
 * 
 * FLUJO DE DATOS:
 * - CREACIÓN:    agregar_pedido.jsp → PedidoServlet?action=guardar → PedidoDAO.registrarPedido() → INSERT en tabla PEDIDOS_PROVEEDOR
 * - LECTURA:     PedidoDAO.listarPedidos(idInventario) → PedidoServlet?action=listar → visualizar_pedidos.jsp (${pedido.totalPedido})
 * - EN INFORMES: InformeDAO.obtenerResumenPedidos() → InformeServlet → visualizar_informes.jsp
 * 
 * TABLAS RELACIONADAS:
 * - INVENTARIO: Cada pedido pertenece a un inventario (FK: id_inventario, viene de la sesión)
 * - PROVEEDOR: Cada pedido se hace a un proveedor (FK: id_proveedor)
 * - DETALLE_PEDIDOS: Los productos específicos pedidos (FK: id_pedido_base)
 */
public class PedidoProveedor {

    // =====================================================================
    // ATRIBUTOS DE LA BD - Columnas de la tabla PEDIDOS_PROVEEDOR
    // =====================================================================

    private int idPedidoBase;     // PK: id_pedido_base (INT, AUTO_INCREMENT). Identificador único del pedido.
    private Date fechaPedido;     // fecha_pedido (DATE). Viene de: agregar_pedido.jsp → input name="fecha_pedido"
    private Date fechaEntrega;    // fecha_entrega (DATE). Viene de: agregar_pedido.jsp → input name="fecha_entrega"
    private double totalPedido;   // total_pedido (DECIMAL). Se calcula como: subtotal + iva. Calculado en JS en agregar_pedido.jsp
    private double subtotal;      // subtotal (DECIMAL). Viene de: agregar_pedido.jsp → input name="subtotal"
    private double ivaPedido;     // iva_pedido (DECIMAL). Viene de: agregar_pedido.jsp → input name="iva"
    private int idInventario;     // FK: id_inventario (INT). Viene de: session.getAttribute("idInventarioActual") en PedidoServlet
    private int idProveedor;      // FK: id_proveedor (INT). Viene de: agregar_pedido.jsp → select name="id_proveedor"

    // =====================================================================
    // ATRIBUTOS AUXILIARES - NO son columnas de la BD
    // =====================================================================

    private String nombreProveedor; // Nombre del proveedor para mostrar en visualizar_pedidos.jsp (${pedido.nombreProveedor})
    private List<DetallePedido> detalles; // Lista de productos detalle del pedido. Cargada en PedidoDAO para mostrar en la vista.

    /**
     * CONSTRUCTOR VACÍO
     * Usado por: PedidoDAO.listarPedidos() al crear objetos desde ResultSet.
     */
    public PedidoProveedor() {
    }

    /**
     * CONSTRUCTOR COMPLETO
     * Permite crear un PedidoProveedor con todos los datos de la BD.
     */
    public PedidoProveedor(int idPedidoBase, Date fechaPedido, Date fechaEntrega, double totalPedido, double subtotal, double ivaPedido, int idInventario, int idProveedor) {
        this.idPedidoBase = idPedidoBase;   // Viene de: rs.getInt("id_pedido_base")
        this.fechaPedido = fechaPedido;     // Viene de: rs.getDate("fecha_pedido")
        this.fechaEntrega = fechaEntrega;   // Viene de: rs.getDate("fecha_entrega")
        this.totalPedido = totalPedido;     // Viene de: rs.getDouble("total_pedido")
        this.subtotal = subtotal;           // Viene de: rs.getDouble("subtotal")
        this.ivaPedido = ivaPedido;         // Viene de: rs.getDouble("iva_pedido")
        this.idInventario = idInventario;   // Viene de: rs.getInt("id_inventario")
        this.idProveedor = idProveedor;     // Viene de: rs.getInt("id_proveedor")
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** PK del pedido. Usado en JSP como: ${pedido.idPedidoBase} */
    public int getIdPedidoBase() {
        return idPedidoBase;
    }

    /** Asigna el ID. Llamado desde: PedidoDAO con rs.getInt("id_pedido_base") */
    public void setIdPedidoBase(int idPedidoBase) {
        this.idPedidoBase = idPedidoBase;
    }

    /** Fecha del pedido. Usado en JSP como: ${pedido.fechaPedido} */
    public Date getFechaPedido() {
        return fechaPedido;
    }

    /** Asigna fecha pedido. Viene de: PedidoServlet → Date.valueOf(request.getParameter("fecha_pedido")) */
    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    /** Fecha de entrega estimada. Usado en JSP como: ${pedido.fechaEntrega} */
    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    /** Asigna fecha entrega. Viene de: PedidoServlet → Date.valueOf(request.getParameter("fecha_entrega")) */
    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    /** Total del pedido (subtotal + IVA). Usado en JSP como: ${pedido.totalPedido} */
    public double getTotalPedido() {
        return totalPedido;
    }

    /** Asigna total. Viene de: PedidoServlet → Double.parseDouble(request.getParameter("total_pedido")) */
    public void setTotalPedido(double totalPedido) {
        this.totalPedido = totalPedido;
    }

    /** Subtotal sin IVA. Usado en vistas de detalle */
    public double getSubtotal() {
        return subtotal;
    }

    /** Asigna subtotal. Viene de: PedidoServlet → Double.parseDouble(request.getParameter("subtotal")) */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /** IVA del pedido. Usado en vistas de detalle */
    public double getIvaPedido() {
        return ivaPedido;
    }

    /** Asigna IVA. Viene de: PedidoServlet → Double.parseDouble(request.getParameter("iva")) */
    public void setIvaPedido(double ivaPedido) {
        this.ivaPedido = ivaPedido;
    }

    /** FK al inventario. Usado internamente por PedidoDAO para vincular pedidos */
    public int getIdInventario() {
        return idInventario;
    }

    /** Asigna inventario. Viene de: PedidoServlet → session.getAttribute("idInventarioActual") */
    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    /** FK al proveedor. Usado para filtrar pedidos por proveedor */
    public int getIdProveedor() {
        return idProveedor;
    }

    /** Asigna proveedor. Viene de: PedidoServlet → Integer.parseInt(request.getParameter("id_proveedor")) */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /** Nombre del proveedor (auxiliar). Usado en JSP como: ${pedido.nombreProveedor} en visualizar_pedidos.jsp */
    public String getNombreProveedor() {
        return nombreProveedor;
    }

    /** Asigna nombre proveedor. Viene de: PedidoDAO con JOIN a tabla PROVEEDOR, rs.getString("nombre_proveedor") */
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    /** Lista de detalles (productos pedidos). Usado para mostrar desglose del pedido */
    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    /** Asigna detalles. Llamado desde: PedidoDAO al cargar los detalles del pedido */
    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }
}
