package com.inventario.model; // Paquete donde viven las clases molde

/**
 * Clase DetalleVenta (Modelo / POJO).
 * 
 * Es el molde que representa UN producto vendido dentro de UNA venta.
 * Por ejemplo: "En la factura #5, vendimos 3 Coca-Colas a $3.000 c/u = $9.000".
 * Corresponde a la tabla 'detalle_venta' de la base de datos.
 */
public class DetalleVenta { // Declaración de la clase pública

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Cada uno es una columna de la tabla)
    // =====================================================================

    private int idDetalleVenta;   // ID único de este renglón de la factura (Clave Primaria)
    private int idVenta;          // ID de la venta o factura a la que pertenece (Clave Foránea hacia tabla VENTA)
    private int idInvDetalle;     // ID que lo relaciona con el producto en el inventario (Clave Foránea hacia INVENTARIO_DETALLE)
    private int cantidad;         // Cuántas unidades se vendieron (ej: 3 cocas)

    private double subtotal;      // El precio parcial de este renglón (ej: 3 x $3.000 = $9.000)

    // =====================================================================
    // ATRIBUTOS AUXILIARES (Solo se usan en pantalla, no están en esta tabla directamente)
    // =====================================================================

    private String nombreProducto; // Nombre del producto vendido (ej: "Coca-Cola"), traído de otra tabla
    private int idProducto;        // ID del producto para buscarlo en el catálogo durante el carrito

    /**
     * CONSTRUCTOR VACÍO.
     * Crea un DetalleVenta sin datos, como un tiquete en blanco.
     */
    public DetalleVenta() {
    }

    // =====================================================================
    // GETTERS Y SETTERS (Para leer y escribir atributos protegidos)
    // =====================================================================

    /** Devuelve el ID único de este renglón de venta */
    public int getIdDetalleVenta() {
        return idDetalleVenta;
    }

    /** Guarda el ID único de este renglón de venta */
    public void setIdDetalleVenta(int idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    /** Devuelve el ID de la venta/factura a la que pertenece */
    public int getIdVenta() {
        return idVenta;
    }

    /** Guarda el ID de la venta/factura */
    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    /** Devuelve el ID de la relación con el inventario */
    public int getIdInvDetalle() {
        return idInvDetalle;
    }

    /** Guarda el ID de la relación con el inventario */
    public void setIdInvDetalle(int idInvDetalle) {
        this.idInvDetalle = idInvDetalle;
    }

    /** Devuelve cuántas unidades se vendieron */
    public int getCantidad() {
        return cantidad;
    }

    /** Guarda cuántas unidades se vendieron */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /** Devuelve el subtotal (cantidad x precio) */
    public double getSubtotal() {
        return subtotal;
    }

    /** Guarda el subtotal de este renglón */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    // =====================================================================
    // GETTERS Y SETTERS AUXILIARES (Datos extra para la pantalla)
    // =====================================================================

    /** Devuelve el nombre del producto vendido (ej: "Coca-Cola") */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /** Guarda el nombre del producto (viene del catálogo) */
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    /** Devuelve el ID del producto para buscarlo en el catálogo */
    public int getIdProducto() {
        return idProducto;
    }

    /** Guarda el ID del producto (se usa mientras el carrito está en memoria) */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
}
