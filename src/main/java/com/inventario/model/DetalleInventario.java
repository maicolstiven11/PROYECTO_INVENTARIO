package com.inventario.model;

/**
 * MODELO: Clase DetalleInventario (Entidad/POJO)
 * 
 * Representa la tabla INVENTARIO_DETALLE de la base de datos MySQL.
 * Cada fila vincula UN producto con UN inventario, almacenando su stock.
 * Esta tabla es el CENTRO del sistema de control de stock.
 * 
 * FLUJO DE DATOS:
 * - CREACIÓN:    inventario_detalle.jsp → InventarioServlet?action=guardar_stock → DetalleInventarioDAO.registrarDetalle() → INSERT
 * - LECTURA:     DetalleInventarioDAO.listarDetalles(idInventario) → Servlets → Vistas (para selects de productos)
 * - RESTA STOCK: VentaDAO.registrarVenta() → UPDATE cantidad_inicial = cantidad_inicial - cantidadVendida
 * - SUMA STOCK:  PedidoDAO.registrarPedido() → UPDATE cantidad_inicial = cantidad_inicial + cantidadPedida
 * - CIERRE:      InventarioDAO.cerrarInventario() → Compara cantidad_inicial con ventas para reporte de descuadre
 * 
 * TABLAS RELACIONADAS:
 * - INVENTARIO: Pertenece a un inventario (FK: id_inventario)
 * - PRODUCTO: Referencia al producto (FK: id_producto)
 * - DETALLE_VENTA: Las ventas referencian esta tabla (FK: id_inv_detalle)
 * - DETALLE_PEDIDOS: Los pedidos referencian esta tabla (FK: id_inv_detalle)
 */
public class DetalleInventario {

    // =====================================================================
    // ATRIBUTOS DE LA BD - Columnas de la tabla INVENTARIO_DETALLE
    // =====================================================================

    private int idDetalle;         // PK: id_detalle (INT, AUTO_INCREMENT). Identificador único de este registro de stock.
    private int idInventario;      // FK: id_inventario (INT). Viene de: session.getAttribute("idInventarioActual") en InventarioServlet
    private int idProducto;        // FK: id_producto (INT). Viene de: inventario_detalle.jsp → input hidden name="id_producto"
    private double cantidadInicial;// cantidad_inicial (DECIMAL). Stock cargado al inicio. Viene de: inventario_detalle.jsp → input name="cantidad"
    private double cantidadFinal;  // cantidad_final (DECIMAL). Stock al cerrar inventario. Se calcula en el cierre.

    // =====================================================================
    // ATRIBUTOS AUXILIARES - NO son columnas de la BD
    // =====================================================================

    private String nombreProducto; // Nombre del producto para mostrar en selects y tablas. Viene de: JOIN con tabla PRODUCTO.
    private double precioUnitario; // Precio unitario del producto. Viene de: JOIN con tabla PRODUCTO. Usado en reporte_descuadre.jsp

    /**
     * CONSTRUCTOR VACÍO
     * Usado por: DetalleInventarioDAO al crear objetos desde ResultSet.
     */
    public DetalleInventario() {}

    // =====================================================================
    // GETTERS Y SETTERS - Atributos de BD
    // =====================================================================

    /** PK del detalle. Usado en JSP como value del select: ${item.idDetalle} en agregar_pedido.jsp */
    public int getIdDetalle() { return idDetalle; }

    /** Asigna el ID. Llamado desde: DetalleInventarioDAO con rs.getInt("id_detalle") */
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    /** FK al inventario. Usado internamente para filtrar stock por inventario */
    public int getIdInventario() { return idInventario; }

    /** Asigna inventario. Viene de: InventarioServlet → session.getAttribute("idInventarioActual") */
    public void setIdInventario(int idInventario) { this.idInventario = idInventario; }

    /** FK al producto. Usado para buscar en VentaDAO al registrar venta: WHERE id_producto = ? AND id_inventario = ? */
    public int getIdProducto() { return idProducto; }

    /** Asigna producto. Viene de: InventarioServlet → request.getParameterValues("id_producto")[i] */
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    /** Stock actual. Se RESTA al vender (VentaDAO) y se SUMA al recibir pedidos (PedidoDAO) */
    public double getCantidadInicial() { return cantidadInicial; }

    /** Asigna stock inicial. Viene de: InventarioServlet → Integer.parseInt(request.getParameterValues("cantidad")[i]) */
    public void setCantidadInicial(double cantidadInicial) { this.cantidadInicial = cantidadInicial; }

    /** Stock al cierre. Usado en reporte_descuadre.jsp para calcular diferencia (faltante/sobrante) */
    public double getCantidadFinal() { return cantidadFinal; }

    /** Asigna stock final. Se calcula en InventarioDAO.cerrarInventario() */
    public void setCantidadFinal(double cantidadFinal) { this.cantidadFinal = cantidadFinal; }

    // =====================================================================
    // GETTERS Y SETTERS - Atributos AUXILIARES
    // =====================================================================

    /** Nombre del producto. Viene de: JOIN con PRODUCTO. Usado en: ${item.nombreProducto} en agregar_pedido.jsp */
    public String getNombreProducto() { return nombreProducto; }

    /** Asigna nombre. Llamado desde: DetalleInventarioDAO con rs.getString("nombre") del JOIN */
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    /** Precio unitario del producto. Usado en reporte_descuadre.jsp: ${det.precioUnitario} para calcular descuadre en dinero */
    public double getPrecioUnitario() { return precioUnitario; }

    /** Asigna precio. Viene de: JOIN con PRODUCTO, rs.getDouble("precio_unitario") */
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
}
