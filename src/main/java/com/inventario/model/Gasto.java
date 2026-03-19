package com.inventario.model; // Paquete donde viven las clases molde

import java.sql.Date; // Importamos la clase Date de SQL para manejar fechas compatibles con la base de datos

/**
 * Clase Gasto (Modelo / POJO).
 * 
 * Es el molde que representa UN gasto del negocio.
 * Por ejemplo: "Compré 2 traperos el 15 de marzo por $8.000".
 * Corresponde a la tabla 'gasto_diario' de la base de datos.
 */
public class Gasto { // Declaración de la clase pública

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Cada uno es una columna de la tabla)
    // =====================================================================

    private int id_gastos;       // ID único del gasto (Clave Primaria, la genera la BD automáticamente)
    private int id_inventario;   // ID del inventario al que pertenece este gasto (Clave Foránea hacia tabla INVENTARIO)
    private int cantidad;        // Cuántos artículos compré (ej: 2 traperos)
    private Date fecha;          // Fecha en que se hizo el gasto (ej: 2026-03-15)
    private Double subtotal;     // Cuánto costó en total (ej: $8.000). Usamos Double (objeto) por si llega nulo
    private String descripcion;  // Para qué fue el gasto (ej: "Materiales de limpieza")

    /**
     * CONSTRUCTOR VACÍO.
     * Crea un Gasto sin datos, como un recibo en blanco.
     * Los datos se llenan después usando los setters.
     */
    public Gasto() {
    }

    // =====================================================================
    // GETTERS Y SETTERS (Leer y escribir cada atributo de forma segura)
    // =====================================================================

    /** Devuelve el ID del gasto */
    public int getId_gastos() {
        return id_gastos;
    }

    /** Guarda el ID del gasto */
    public void setId_gastos(int id_gastos) {
        this.id_gastos = id_gastos;
    }

    /** Devuelve el ID del inventario al que pertenece */
    public int getId_inventario() {
        return id_inventario;
    }

    /** Guarda el ID del inventario al que pertenece */
    public void setId_inventario(int id_inventario) {
        this.id_inventario = id_inventario;
    }

    /** Devuelve la cantidad de artículos comprados */
    public int getCantidad() {
        return cantidad;
    }

    /** Guarda la cantidad de artículos comprados */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /** Devuelve la fecha en que se realizó el gasto */
    public Date getFecha() {
        return fecha;
    }

    /** Guarda la fecha del gasto */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /** Devuelve el costo total del gasto */
    public Double getSubtotal() {
        return subtotal;
    }

    /** Guarda el costo total del gasto */
    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    /** Devuelve la descripción del gasto (ej: "Materiales de limpieza") */
    public String getDescripcion() {
        return descripcion;
    }

    /** Guarda la descripción del gasto */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
