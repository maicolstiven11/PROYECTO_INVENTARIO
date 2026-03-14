package com.inventario.model;

/**
 * MODELO: Clase DetalleVenta (Entidad/POJO)
 * 
 * Representa la tabla DETALLE_VENTA de la base de datos MySQL.
 * Cada fila es UN producto vendido dentro de una Venta.
 * 
 * FLUJO DE DATOS:
 * - CREACIÓN:    agregar_venta.jsp → VentaServlet?action=agregar → Se agrega al carrito (List en sesión)
 *                VentaServlet?action=finalizar → VentaDAO.registrarVenta() → INSERT en tabla DETALLE_VENTA
 * - LECTURA:     VentaDAO.obtenerDetallesVenta(idVenta) → detalle_venta.jsp (${det.nombreProducto}, ${det.cantidad})
 * 
 * TABLAS RELACIONADAS:
 * - VENTA: Cada detalle pertenece a una venta (FK: id_venta)
 * - INVENTARIO_DETALLE: Referencia al producto en el inventario (FK: id_inv_detalle). Controla stock.
 */
public class DetalleVenta {

    // =====================================================================
    // ATRIBUTOS DE LA BD - Columnas de la tabla DETALLE_VENTA
    // =====================================================================

    private int idDetalleVenta;   // PK: id_detalle_venta (INT, AUTO_INCREMENT)
    private int idVenta;          // FK: id_venta (INT). Referencia a la tabla VENTA. Se asigna en VentaDAO al registrar.
    private int idInvDetalle;     // FK: id_inv_detalle (INT). Referencia a INVENTARIO_DETALLE (producto en el inventario activo).
    private int cantidad;         // cantidad (INT). Cuántas unidades se vendieron. Viene de: agregar_venta.jsp → input name="cantidad"
    private double precioUnitario;// precio_unitario (DECIMAL). Precio del producto al momento de la venta. Viene de: Producto.getPrecioUnitario()
    private double subtotal;      // subtotal (DECIMAL). Se calcula como: cantidad * precioUnitario

    // =====================================================================
    // ATRIBUTOS AUXILIARES - NO son columnas de la BD, son para la vista
    // =====================================================================

    private String nombreProducto; // Nombre del producto para mostrar en la tabla del carrito (agregar_venta.jsp: ${item.nombreProducto})
    private int idProducto;        // ID del producto original, auxiliar para buscar en INVENTARIO_DETALLE por id_producto

    /**
     * CONSTRUCTOR VACÍO
     * Usado por: VentaServlet al crear items del carrito con new DetalleVenta()
     * Usado por: VentaDAO al leer detalles de una venta desde la BD
     */
    public DetalleVenta() {
    }

    // =====================================================================
    // GETTERS Y SETTERS - Atributos de BD
    // =====================================================================

    /** PK del detalle. Usado internamente por VentaDAO */
    public int getIdDetalleVenta() {
        return idDetalleVenta;
    }

    /** Asigna el ID. Llamado desde: VentaDAO con rs.getInt("id_detalle_venta") */
    public void setIdDetalleVenta(int idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    /** FK a la venta padre. Asignado en VentaDAO.registrarVenta() después de obtener el id_venta generado */
    public int getIdVenta() {
        return idVenta;
    }

    /** Asigna la venta. Llamado desde: VentaDAO al insertar detalles */
    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    /** FK a INVENTARIO_DETALLE. Identifica qué producto del inventario se vendió. Se resuelve en VentaDAO */
    public int getIdInvDetalle() {
        return idInvDetalle;
    }

    /** Asigna el id_inv_detalle. Resuelto en VentaDAO buscando por id_producto + id_inventario en sesión */
    public void setIdInvDetalle(int idInvDetalle) {
        this.idInvDetalle = idInvDetalle;
    }

    /** Cantidad vendida. Usado en JSP como: ${item.cantidad} en la tabla del carrito */
    public int getCantidad() {
        return cantidad;
    }

    /** Asigna cantidad. Viene de: VentaServlet → Integer.parseInt(request.getParameter("cantidad")) */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /** Precio unitario al momento de la venta. Usado en JSP como: ${item.precioUnitario} */
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    /** Asigna precio. Viene de: ProductoDAO → producto.getPrecioUnitario() en VentaServlet */
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /** Subtotal de esta línea (cantidad * precio). Usado en JSP como: ${item.subtotal} */
    public double getSubtotal() {
        return subtotal;
    }

    /** Asigna subtotal. Se calcula en VentaServlet como: cantidad * precioUnitario */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    // =====================================================================
    // GETTERS Y SETTERS - Atributos AUXILIARES (no están en la BD)
    // =====================================================================

    /** Nombre del producto para mostrar en el carrito. Viene de: ProductoDAO → producto.getNombre() */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /** Asigna nombre para la vista. Llamado desde: VentaServlet al agregar al carrito */
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    /** ID del producto original (tabla PRODUCTO). Auxiliar para buscar en INVENTARIO_DETALLE */
    public int getIdProducto() {
        return idProducto;
    }

    /** Asigna ID producto. Viene de: VentaServlet → Integer.parseInt(request.getParameter("id_producto")) */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
}
