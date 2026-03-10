package com.inventario.model;

public class DetalleVenta {
    private int idDetalleVenta;
    private int idVenta;
    private int idInvDetalle;   // CAMBIADO: antes era idProducto, ahora referencia a inventario_detalle
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleVenta() {
    }

    public int getIdDetalleVenta() {
        return idDetalleVenta;
    }

    public void setIdDetalleVenta(int idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    // CAMBIADO: getter/setter de idInvDetalle (antes era idProducto)
    public int getIdInvDetalle() {
        return idInvDetalle;
    }

    public void setIdInvDetalle(int idInvDetalle) {
        this.idInvDetalle = idInvDetalle;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    // Campos auxiliares para vista (No en BD)
    private String nombreProducto;
    private int idProducto; // auxiliar para el carrito (para buscar en inventario_detalle)

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
}
