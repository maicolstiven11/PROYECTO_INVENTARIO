package com.inventario.model; // Paquete donde viven las clases molde

/**
 * Clase Proveedor (Modelo / POJO).
 * 
 * Es el molde que representa UN proveedor o vendedor externo.
 * Por ejemplo: "Bavaria, contacto: Don Ramón, tel: 3123891045, correo: ventas@bavaria.com".
 * Corresponde a la tabla 'datos_proveedor' de la base de datos.
 */
public class Proveedor { // Declaración de la clase pública

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Columnas de la tabla)
    // =====================================================================

    private int idProveedor;         // ID único del proveedor (Clave Primaria)
    private String nombreProveedor;  // Nombre de la empresa (ej: "Bavaria")
    private String contacto;         // Persona de contacto (ej: "Don Ramón")
    private String telefono;         // Teléfono de contacto (ej: "3123891045")
    private String correo;           // Correo electrónico (ej: "ventas@bavaria.com")

    /**
     * CONSTRUCTOR VACÍO.
     * Crea un Proveedor sin datos, como una tarjeta de presentación en blanco.
     */
    public Proveedor() {
    }

    /**
     * CONSTRUCTOR CON PARÁMETROS (Sobrecarga).
     * Crea un Proveedor con todos los datos de una sola vez.
     * 
     * @param idProveedor ID del proveedor
     * @param nombreProveedor Nombre de la empresa
     * @param contacto Persona de contacto
     * @param telefono Número de teléfono
     * @param correo Correo electrónico
     */
    public Proveedor(int idProveedor, String nombreProveedor, String contacto, String telefono, String correo) {
        this.idProveedor = idProveedor;           // Guarda el ID
        this.nombreProveedor = nombreProveedor;   // Guarda el nombre de la empresa
        this.contacto = contacto;                 // Guarda la persona de contacto
        this.telefono = telefono;                 // Guarda el teléfono
        this.correo = correo;                     // Guarda el correo electrónico
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** Devuelve el ID del proveedor */
    public int getIdProveedor() {
        return idProveedor;
    }

    /** Guarda el ID del proveedor */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /** Devuelve el nombre de la empresa proveedora */
    public String getNombreProveedor() {
        return nombreProveedor;
    }

    /** Guarda el nombre de la empresa proveedora */
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    /** Devuelve el nombre de la persona de contacto */
    public String getContacto() {
        return contacto;
    }

    /** Guarda el nombre de la persona de contacto */
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    /** Devuelve el teléfono del proveedor */
    public String getTelefono() {
        return telefono;
    }

    /** Guarda el teléfono del proveedor */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /** Devuelve el correo electrónico del proveedor */
    public String getCorreo() {
        return correo;
    }

    /** Guarda el correo electrónico */
    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
