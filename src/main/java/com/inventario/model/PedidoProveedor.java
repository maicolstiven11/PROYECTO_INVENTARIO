package com.inventario.model;

import java.sql.Date;
import java.util.List;

public class PedidoProveedor {
    private int idPedidoBase;
    private Date fechaPedido;
    private Date fechaEntrega;
    private double totalPedido;
    private double ivaPedido;
    private double subtotal;
    private int idInventario;
    private int idProveedor;
    
    // Atributo auxiliar para mostrar nombre del proveedor en listados
    private String nombreProveedor;
    
    // Lista de detalles asociada
    private List<DetallePedido> detalles;

    public PedidoProveedor() {
    }

    public PedidoProveedor(int idPedidoBase, Date fechaPedido, Date fechaEntrega, double totalPedido, double ivaPedido, double subtotal, int idInventario, int idProveedor) {
        this.idPedidoBase = idPedidoBase;
        this.fechaPedido = fechaPedido;
        this.fechaEntrega = fechaEntrega;
        this.totalPedido = totalPedido;
        this.ivaPedido = ivaPedido;
        this.subtotal = subtotal;
        this.idInventario = idInventario;
        this.idProveedor = idProveedor;
    }

    // Getters y Setters
    public int getIdPedidoBase() {
        return idPedidoBase;
    }

    public void setIdPedidoBase(int idPedidoBase) {
        this.idPedidoBase = idPedidoBase;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public double getTotalPedido() {
        return totalPedido;
    }

    public void setTotalPedido(double totalPedido) {
        this.totalPedido = totalPedido;
    }

    public double getIvaPedido() {
        return ivaPedido;
    }

    public void setIvaPedido(double ivaPedido) {
        this.ivaPedido = ivaPedido;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }
}
