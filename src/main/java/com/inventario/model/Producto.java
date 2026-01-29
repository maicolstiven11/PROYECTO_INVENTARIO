package com.inventario.model;

import java.sql.Date; // Usamos esto para el campo fecha_vencimiento

public class Producto {
    // ATRIBUTOS (Espejo de la tabla PRODUCTO)
    private int idProducto;
    private String nombre;
    private String marca;
    private double precioUnitario; // 'double' es ideal para precios (decimales)
    private String tipo;
    private String imagen;         // Guardaremos la ruta o nombre del archivo de imagen
    private Date fechaVencimiento;
    private String cantidadMedida; // Ej: "1 Litro", "500gr"

    // CONSTRUCTOR VACÍO
    public Producto() {
    }

    // CONSTRUCTOR COMPLETO
    public Producto(int idProducto, String nombre, String marca, double precioUnitario, String tipo, String imagen, Date fechaVencimiento, String cantidadMedida) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.marca = marca;
        this.precioUnitario = precioUnitario;
        this.tipo = tipo;
        this.imagen = imagen;
        this.fechaVencimiento = fechaVencimiento;
        this.cantidadMedida = cantidadMedida;
    }

    // GETTERS Y SETTERS
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getCantidadMedida() {
        return cantidadMedida;
    }

    public void setCantidadMedida(String cantidadMedida) {
        this.cantidadMedida = cantidadMedida;
    }
}
