package com.inventario.model;

public class DetallePedido {
    private int idPedidoRegistro;
    private int idPedidoBase;
    private int idInvDetalle;   // CAMBIADO: antes era idProducto, ahora referencia a inventario_detalle
    private int cantidadPedida;
    private double precioUnitarioReal;

    // Atributos auxiliares para mostrar información del producto
    private String nombreProducto;
    private double subtotalCalculado; // (cantidad * precioUnitarioReal)

    public DetallePedido() {
    }

    public DetallePedido(int idPedidoRegistro, int idPedidoBase, int idInvDetalle, int cantidadPedida, double precioUnitarioReal) {
        this.idPedidoRegistro = idPedidoRegistro;
        this.idPedidoBase = idPedidoBase;
        this.idInvDetalle = idInvDetalle;
        this.cantidadPedida = cantidadPedida;
        this.precioUnitarioReal = precioUnitarioReal;
    }

    // Getters y Setters
    public int getIdPedidoRegistro() {
        return idPedidoRegistro;
    }

    public void setIdPedidoRegistro(int idPedidoRegistro) {
        this.idPedidoRegistro = idPedidoRegistro;
    }

    public int getIdPedidoBase() {
        return idPedidoBase;
    }

    public void setIdPedidoBase(int idPedidoBase) {
        this.idPedidoBase = idPedidoBase;
    }

    // CAMBIADO: getter/setter de idInvDetalle (antes era idProducto)
    public int getIdInvDetalle() {
        return idInvDetalle;
    }

    public void setIdInvDetalle(int idInvDetalle) {
        this.idInvDetalle = idInvDetalle;
    }

    public int getCantidadPedida() {
        return cantidadPedida;
    }

    public void setCantidadPedida(int cantidadPedida) {
        this.cantidadPedida = cantidadPedida;
    }

    public double getPrecioUnitarioReal() {
        return precioUnitarioReal;
    }

    public void setPrecioUnitarioReal(double precioUnitarioReal) {
        this.precioUnitarioReal = precioUnitarioReal;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public double getSubtotalCalculado() {
        return subtotalCalculado;
    }

    public void setSubtotalCalculado(double subtotalCalculado) {
        this.subtotalCalculado = subtotalCalculado;
    }
}
