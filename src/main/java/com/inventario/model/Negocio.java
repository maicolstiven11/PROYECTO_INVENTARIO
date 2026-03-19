package com.inventario.model; // Paquete donde viven las clases molde

/**
 * Clase Negocio (Modelo / POJO).
 * 
 * Es el molde que representa UN local comercial o tienda.
 * Por ejemplo: "Bar de Moe, ubicado en Calle 5 #12-30, estado: activo".
 * Corresponde a la tabla 'negocio' de la base de datos.
 * Es la clase padre principal: sin un Negocio, no existen inventarios, ventas ni gastos.
 */
public class Negocio { // Declaración de la clase pública

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Columnas de la tabla)
    // =====================================================================

    private int idNegocio;                   // ID único del negocio (Clave Primaria)
    private String nombre;                   // Nombre del local (ej: "Bar de Moe")
    private String direccion;                // Dirección física (ej: "Calle 5 #12-30")
    private String estado;                   // Estado del negocio: "activo" o "inactivo"
    
    // =====================================================================
    // ATRIBUTO AUXILIAR (No es columna de la tabla, se calcula en el programa)
    // =====================================================================
    
    private boolean tieneInventarioActivo;   // true si el bar tiene un inventario abierto, false si no

    /**
     * CONSTRUCTOR VACÍO.
     * Crea un Negocio sin datos, como un local sin nombre todavía.
     */
    public Negocio() {
    }

    /**
     * CONSTRUCTOR CON PARÁMETROS (Sobrecarga).
     * Crea un Negocio con todos sus datos de una sola vez.
     * 
     * @param idNegocio ID del negocio
     * @param nombre Nombre del local
     * @param direccion Dirección del local
     * @param estado "activo" o "inactivo"
     */
    public Negocio(int idNegocio, String nombre, String direccion, String estado) {
        this.idNegocio = idNegocio;   // Guarda el ID
        this.nombre = nombre;         // Guarda el nombre
        this.direccion = direccion;   // Guarda la dirección
        this.estado = estado;         // Guarda el estado
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** Devuelve el ID del negocio */
    public int getIdNegocio() {
        return idNegocio;
    }

    /** Guarda el ID del negocio */
    public void setIdNegocio(int idNegocio) {
        this.idNegocio = idNegocio;
    }

    /** Devuelve el nombre del negocio */
    public String getNombre() {
        return nombre;
    }

    /** Guarda el nombre del negocio */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** Devuelve la dirección del negocio */
    public String getDireccion() {
        return direccion;
    }

    /** Guarda la dirección del negocio */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /** Devuelve el estado ("activo" o "inactivo") */
    public String getEstado() {
        return estado;
    }

    /** Guarda el estado del negocio */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /** 
     * Devuelve true si el negocio tiene un inventario abierto actualmente.
     * Este valor NO viene de la tabla directamente, lo calcula el DAO.
     */
    public boolean isTieneInventarioActivo() {
        return tieneInventarioActivo;
    }

    /** Guarda si el negocio tiene o no un inventario activo */
    public void setTieneInventarioActivo(boolean tieneInventarioActivo) {
        this.tieneInventarioActivo = tieneInventarioActivo;
    }
}
