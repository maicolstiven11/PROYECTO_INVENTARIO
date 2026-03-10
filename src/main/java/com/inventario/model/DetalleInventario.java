package com.inventario.model;

public class DetalleInventario {
    private int idDetalle;
    private int idInventario;
    private int idProducto;
    private double cantidadInicial;
    private double cantidadFinal;
    
    // Auxiliar para la interfaz
    private String nombreProducto;

    public DetalleInventario() {}

    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdInventario() { return idInventario; }
    public void setIdInventario(int idInventario) { this.idInventario = idInventario; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public double getCantidadInicial() { return cantidadInicial; }
    public void setCantidadInicial(double cantidadInicial) { this.cantidadInicial = cantidadInicial; }

    public double getCantidadFinal() { return cantidadFinal; }
    public void setCantidadFinal(double cantidadFinal) { this.cantidadFinal = cantidadFinal; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
}
