package com.inventario.model; // Paquete donde viven las clases molde

import java.sql.Date; // Para manejar la fecha de vencimiento compatible con MySQL

/**
 * Clase Producto (Modelo / POJO).
 * 
 * Es el molde que representa UN artículo del catálogo del negocio.
 * Por ejemplo: "Cerveza Águila, marca Bavaria, precio $3.500, tipo Bebida, vence 2026-12-01".
 * Corresponde a la tabla 'producto' de la base de datos.
 */
public class Producto { // Declaración de la clase pública

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Cada uno es una columna de la tabla)
    // =====================================================================

    private int idProducto;        // ID único del producto (Clave Primaria, la genera la BD)
    private String nombre;         // Nombre del producto (ej: "Cerveza Águila")
    private String marca;          // Marca comercial (ej: "Bavaria")
    private double precioUnitario; // Precio de cada unidad (ej: $3.500)
    private String tipo;           // Categoría del producto (ej: "Bebida", "Paquete", "Enlatado")
    private String imagen;         // Nombre del archivo de la foto (ej: "1710203942_cerveza.jpg")
    private Date fechaVencimiento; // Fecha en que vence el producto (puede ser nulo si no vence)
    private String cantidadMedida; // Descripción del tamaño o peso (ej: "750ml", "200gr")
    private double stok_actual;

    /**
     * CONSTRUCTOR VACÍO.
     * Crea un Producto sin datos, como una etiqueta en blanco.
     */
    public Producto() {
    }

    /**
     * CONSTRUCTOR CON PARÁMETROS (Sobrecarga).
     * Crea un Producto con todos los datos de una sola vez.
     * 
     * @param idProducto ID del producto
     * @param nombre Nombre del producto
     * @param marca Marca comercial
     * @param precioUnitario Precio por unidad
     * @param tipo Categoría
     * @param imagen Nombre del archivo de imagen
     * @param fechaVencimiento Fecha de caducidad
     * @param cantidadMedida Tamaño o peso
     */
    public Producto(int idProducto, String nombre, String marca, double precioUnitario, String tipo, String imagen, Date fechaVencimiento, String cantidadMedida) {
        this.idProducto = idProducto;           // Guarda el ID
        this.nombre = nombre;                   // Guarda el nombre
        this.marca = marca;                     // Guarda la marca
        this.precioUnitario = precioUnitario;   // Guarda el precio
        this.tipo = tipo;                       // Guarda la categoría
        this.imagen = imagen;                   // Guarda el nombre de la foto
        this.fechaVencimiento = fechaVencimiento; // Guarda la fecha de vencimiento
        this.cantidadMedida = cantidadMedida;   // Guarda el tamaño/peso
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================
    public double getStok_actual(){
        return stok_actual;
    }
    
    public void setStok_actual(double stok_actual){
        this.stok_actual = stok_actual;
    }
    
    
    /** Devuelve el ID del producto */
    public int getIdProducto() {
        return idProducto;
    }

    /** Guarda el ID del producto */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    /** Devuelve el nombre del producto (ej: "Cerveza Águila") */
    public String getNombre() {
        return nombre;
    }

    /** Guarda el nombre del producto */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** Devuelve la marca del producto (ej: "Bavaria") */
    public String getMarca() {
        return marca;
    }

    /** Guarda la marca del producto */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /** Devuelve el precio unitario (ej: 3500.0) */
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    /** Guarda el precio unitario */
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /** Devuelve la categoría del producto (ej: "Bebida") */
    public String getTipo() {
        return tipo;
    }

    /** Guarda la categoría del producto */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /** Devuelve el nombre del archivo de imagen (ej: "1710203942_cerveza.jpg") */
    public String getImagen() {
        return imagen;
    }

    /** Guarda el nombre del archivo de imagen */
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    /** Devuelve la fecha de vencimiento (puede ser null si no aplica) */
    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    /** Guarda la fecha de vencimiento */
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /** Devuelve la cantidad/medida (ej: "750ml") */
    public String getCantidadMedida() {
        return cantidadMedida;
    }

    /** Guarda la cantidad/medida */
    public void setCantidadMedida(String cantidadMedida) {
        this.cantidadMedida = cantidadMedida;
    }
}
