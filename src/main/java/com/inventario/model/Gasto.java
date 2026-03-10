package com.inventario.model;

import java.sql.Date;

public class Gasto {
    private int id_gastos;
    private int id_inventario;   // RESTAURADO: de id_negocio a id_inventario
    private int cantidad;
    private Date fecha;
    private Double subtotal;
    private String descripcion;
    
    public Gasto(){
        
    }
    public int getId_gastos(){
        return id_gastos;
    }
    public void setId_gastos(int id_gastos){
        this.id_gastos = id_gastos;
    }
    public int getId_inventario(){
        return id_inventario;
    }
    public void setId_inventario(int id_inventario){
        this.id_inventario = id_inventario;
    }
    public int getCantidad(){
        return cantidad;
    }
    public void setCantidad(int cantidad){
        this.cantidad = cantidad;
    }
    public Date getFecha(){
        return fecha;
    }
    public void setFecha(Date fecha){
        this.fecha = fecha;
    }
    public Double getSubtotal(){
        return subtotal;
    }
    public void setSubtotal(Double subtotal){
        this.subtotal = subtotal;
    }
    public String getDescripcion(){
        return descripcion;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
}
