package com.inventario.model; // Paquete donde viven las clases molde

import java.sql.Date; // Para manejar la fecha de la venta compatible con MySQL

/**
 * Clase Venta (Modelo / POJO).
 * 
 * Es el molde que representa UNA venta o factura del negocio.
 * Por ejemplo: "Venta #12 del inventario de Marzo, total $25.000, fecha 2026-03-18".
 * Corresponde a la tabla 'venta' de la base de datos.
 * Cada venta puede tener varios renglones (DetalleVenta) con los productos vendidos.
 */
public class Venta { // Declaración de la clase pública

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Columnas de la tabla)
    // =====================================================================

    private int idVenta;        // ID único de la venta (Clave Primaria, la genera la BD)
    private int idInventario;   // ID del inventario al que pertenece (Clave Foránea hacia tabla INVENTARIO)
    private double totalVenta;  // El total cobrado en esta factura (ej: $25.000)
    private Date fechaVenta;    // Fecha en que se realizó la venta (ej: 2026-03-18)

    /**
     * CONSTRUCTOR VACÍO.
     * Crea una Venta sin datos, como un recibo nuevo sin llenar.
     */
    public Venta() {
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** Devuelve el ID de la venta */
    public int getIdVenta() {
        return idVenta;
    }

    /** Guarda el ID de la venta */
    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    /** Devuelve el ID del inventario al que pertenece */
    public int getIdInventario() {
        return idInventario;
    }

    /** Guarda el ID del inventario */
    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    /** Devuelve el total cobrado en la venta (ej: $25.000) */
    public double getTotalVenta() {
        return totalVenta;
    }

    /** Guarda el total de la venta */
    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }

    /** Devuelve la fecha en que se hizo la venta */
    public Date getFechaVenta() {
        return fechaVenta;
    }

    /** Guarda la fecha de la venta */
    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
}
