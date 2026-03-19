package com.inventario.model; // Paquete donde viven las clases molde

import java.sql.Date; // Importamos la clase Date de SQL para manejar fechas

/**
 * Clase Inventario (Modelo / POJO).
 * 
 * Es el molde que representa UN periodo contable (por ejemplo: "Inventario de Marzo 2026").
 * Dentro de él se agrupan todas las ventas, gastos y pedidos de ese mes o semana.
 * Corresponde a la tabla 'inventario' de la base de datos.
 */
public class Inventario { // Declaración de la clase pública

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Columnas de la tabla)
    // =====================================================================

    private int idInventario;   // ID único del inventario (Clave Primaria)
    private int idNegocio;      // ID del negocio al que pertenece (Clave Foránea hacia tabla NEGOCIO)
    private Date fechaInicio;   // Fecha en que se abrió este periodo (ej: 2026-03-01)
    private String tipoControl; // Tipo de control: "semanal" o "mensual"
    private String estado;      // Si está "activo" (abierto) o "inactivo" (ya se cerró)

    /**
     * CONSTRUCTOR VACÍO.
     * Crea un Inventario sin datos, como una carpeta nueva sin documentos.
     */
    public Inventario() {
    }

    /**
     * CONSTRUCTOR CON PARÁMETROS (Sobrecarga).
     * Crea un Inventario llenando todos los datos de una sola vez.
     * 
     * @param idInventario ID del inventario
     * @param idNegocio ID del negocio dueño
     * @param fechaInicio Fecha de apertura
     * @param tipoControl "semanal" o "mensual"
     * @param estado "activo" o "inactivo"
     */
    public Inventario(int idInventario, int idNegocio, Date fechaInicio, String tipoControl, String estado) {
        this.idInventario = idInventario; // "this" apunta al atributo de la clase para no confundirlo con el parámetro
        this.idNegocio = idNegocio;       // Guarda a qué negocio pertenece
        this.fechaInicio = fechaInicio;   // Guarda la fecha de apertura
        this.tipoControl = tipoControl;   // Guarda si es semanal o mensual
        this.estado = estado;             // Guarda si está activo o cerrado
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** Devuelve el ID del inventario */
    public int getIdInventario() {
        return idInventario;
    }

    /** Guarda el ID del inventario */
    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    /** Devuelve el ID del negocio al que pertenece */
    public int getIdNegocio() {
        return idNegocio;
    }

    /** Guarda el ID del negocio */
    public void setIdNegocio(int idNegocio) {
        this.idNegocio = idNegocio;
    }

    /** Devuelve la fecha de inicio del periodo */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /** Guarda la fecha de inicio */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /** Devuelve el tipo de control ("semanal" o "mensual") */
    public String getTipoControl() {
        return tipoControl;
    }

    /** Guarda el tipo de control */
    public void setTipoControl(String tipoControl) {
        this.tipoControl = tipoControl;
    }

    /** Devuelve el estado del inventario ("activo" o "inactivo") */
    public String getEstado() {
        return estado;
    }

    /** Guarda el estado del inventario */
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
