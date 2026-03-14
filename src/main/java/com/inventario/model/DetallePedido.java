package com.inventario.model;

/**
 * MODELO: Clase DetallePedido (Entidad/POJO)
 * 
 * Representa la tabla DETALLE_PEDIDOS de la base de datos MySQL.
 * Cada fila es UN producto específico dentro de un Pedido a Proveedor.
 * 
 * FLUJO DE DATOS:
 * - CREACIÓN:    agregar_pedido.jsp → PedidoServlet?action=guardar → PedidoDAO.registrarPedido() → INSERT en tabla DETALLE_PEDIDOS
 * - LECTURA:     PedidoDAO.listarPedidos() → PedidoServlet → visualizar_pedidos.jsp (${det.nombreProducto})
 * 
 * TABLAS RELACIONADAS:
 * - PEDIDOS_PROVEEDOR: Cada detalle pertenece a un pedido (FK: id_pedido_base)
 * - INVENTARIO_DETALLE: Referencia al producto en el inventario (FK: id_inv_detalle). Al recibir pedido, se suma stock.
 */
public class DetallePedido {

    // =====================================================================
    // ATRIBUTOS DE LA BD - Columnas de la tabla DETALLE_PEDIDOS
    // =====================================================================

    private int idPedidoRegistro;     // PK: id_pedido_registro (INT, AUTO_INCREMENT). Identificador único de la línea de detalle.
    private int idPedidoBase;         // FK: id_pedido_base (INT). Referencia al pedido padre en PEDIDOS_PROVEEDOR.
    private int idInvDetalle;         // FK: id_inv_detalle (INT). Referencia al producto en INVENTARIO_DETALLE. Viene de: agregar_pedido.jsp → select name="id_inv_detalle"
    private int cantidadPedida;       // cantidad_pedida (INT). Unidades solicitadas. Viene de: agregar_pedido.jsp → input name="cantidad"
    private double precioUnitarioReal;// precio_unitario_real (DECIMAL). Precio real por unidad. Calculado en JS: (subtotal+iva)/cantidad

    // =====================================================================
    // ATRIBUTOS AUXILIARES - NO son columnas de la BD
    // =====================================================================

    private String nombreProducto;    // Nombre del producto para mostrar en visualizar_pedidos.jsp (${det.nombreProducto})
    private double subtotalCalculado; // Calculado como: cantidadPedida * precioUnitarioReal. Para mostrar en la vista.

    /**
     * CONSTRUCTOR VACÍO
     * Usado por: PedidoDAO al crear objetos desde ResultSet.
     */
    public DetallePedido() {
    }

    /**
     * CONSTRUCTOR COMPLETO (solo atributos de BD)
     */
    public DetallePedido(int idPedidoRegistro, int idPedidoBase, int idInvDetalle, int cantidadPedida, double precioUnitarioReal) {
        this.idPedidoRegistro = idPedidoRegistro;       // Viene de: rs.getInt("id_pedido_registro")
        this.idPedidoBase = idPedidoBase;               // Viene de: rs.getInt("id_pedido_base")
        this.idInvDetalle = idInvDetalle;                // Viene de: rs.getInt("id_inv_detalle")
        this.cantidadPedida = cantidadPedida;            // Viene de: rs.getInt("cantidad_pedida")
        this.precioUnitarioReal = precioUnitarioReal;    // Viene de: rs.getDouble("precio_unitario_real")
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** PK del detalle. Usado internamente por PedidoDAO */
    public int getIdPedidoRegistro() {
        return idPedidoRegistro;
    }

    /** Asigna el ID. Llamado desde: PedidoDAO con rs.getInt("id_pedido_registro") */
    public void setIdPedidoRegistro(int idPedidoRegistro) {
        this.idPedidoRegistro = idPedidoRegistro;
    }

    /** FK al pedido padre. Asignado en PedidoDAO al insertar detalle */
    public int getIdPedidoBase() {
        return idPedidoBase;
    }

    /** Asigna pedido padre. Viene de: PedidoDAO → idGenerado después del INSERT del pedido */
    public void setIdPedidoBase(int idPedidoBase) {
        this.idPedidoBase = idPedidoBase;
    }

    /** FK a INVENTARIO_DETALLE. Identifica qué producto del inventario se pidió */
    public int getIdInvDetalle() {
        return idInvDetalle;
    }

    /** Asigna id_inv_detalle. Viene de: PedidoServlet → Integer.parseInt(request.getParameter("id_inv_detalle")) */
    public void setIdInvDetalle(int idInvDetalle) {
        this.idInvDetalle = idInvDetalle;
    }

    /** Cantidad solicitada. Usado en JSP como: ${det.cantidadPedida} */
    public int getCantidadPedida() {
        return cantidadPedida;
    }

    /** Asigna cantidad. Viene de: PedidoServlet → Integer.parseInt(request.getParameter("cantidad")) */
    public void setCantidadPedida(int cantidadPedida) {
        this.cantidadPedida = cantidadPedida;
    }

    /** Precio unitario real del proveedor. Usado en JSP como: ${det.precioUnitarioReal} */
    public double getPrecioUnitarioReal() {
        return precioUnitarioReal;
    }

    /** Asigna precio. Viene de: PedidoServlet → Double.parseDouble(request.getParameter("precio_unitario")) */
    public void setPrecioUnitarioReal(double precioUnitarioReal) {
        this.precioUnitarioReal = precioUnitarioReal;
    }

    /** Nombre del producto (auxiliar). Viene de: PedidoDAO con JOIN a PRODUCTO vía INVENTARIO_DETALLE */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /** Asigna nombre. Llamado desde: PedidoDAO con rs.getString("nombre") del JOIN */
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    /** Subtotal calculado (auxiliar). Calculado como: cantidadPedida * precioUnitarioReal */
    public double getSubtotalCalculado() {
        return subtotalCalculado;
    }

    /** Asigna subtotal. Calculado en PedidoDAO o en la vista */
    public void setSubtotalCalculado(double subtotalCalculado) {
        this.subtotalCalculado = subtotalCalculado;
    }
}
