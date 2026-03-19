package com.inventario.model; // Paquete donde viven todas las clases "molde" del proyecto

/**
 * Clase DetalleInventario (Modelo / POJO).
 * 
 * Es el molde que representa UN producto dentro de UN inventario.
 * Por ejemplo: "En el inventario de Marzo, la Cerveza Águila tenía 50 al inicio y quedaron 30 al final."
 * Corresponde a la tabla 'inventario_detalle' de la base de datos.
 */
public class DetalleInventario { // Clase pública que cualquiera puede usar

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Las cajitas internas donde se guarda la información)
    // Cada uno corresponde a una columna de la tabla en la base de datos.
    // =====================================================================

    private int idDetalle;         // Número identificador único de este registro (Clave Primaria en la tabla)
    private int idInventario;      // Número que indica a qué inventario pertenece (Clave Foránea hacia tabla INVENTARIO)
    private int idProducto;        // Número que indica qué producto es (Clave Foránea hacia tabla PRODUCTO)
    private double cantidadInicial;// Cuántas unidades había cuando se empezó el inventario
    private double cantidadFinal;  // Cuántas unidades quedaron al cerrar el inventario

    // =====================================================================
    // ATRIBUTOS AUXILIARES (No están en la tabla directamente, pero los usamos para mostrar datos en pantalla)
    // Se llenan cuando el DAO hace consultas que cruzan tablas (JOINs).
    // =====================================================================

    private String nombreProducto; // El nombre del producto (ej: "Cerveza Águila"), viene de la tabla PRODUCTO
    private double precioUnitario; // El precio del producto, también viene de la tabla PRODUCTO

    /**
     * CONSTRUCTOR VACÍO.
     * Crea un objeto DetalleInventario sin datos, como una ficha en blanco.
     * Después le llenamos los datos uno por uno con los setters.
     */
    public DetalleInventario() {}

    // =====================================================================
    // GETTERS Y SETTERS
    // Los getters sirven para LEER un dato y los setters para ESCRIBIR un dato.
    // Así protegemos los atributos privados (Encapsulamiento).
    // =====================================================================

    /** Devuelve el ID único de este detalle */
    public int getIdDetalle() { return idDetalle; }

    /** Guarda el ID único de este detalle */
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    /** Devuelve el ID del inventario al que pertenece */
    public int getIdInventario() { return idInventario; }

    /** Guarda el ID del inventario al que pertenece */
    public void setIdInventario(int idInventario) { this.idInventario = idInventario; }

    /** Devuelve el ID del producto asociado */
    public int getIdProducto() { return idProducto; }

    /** Guarda el ID del producto asociado */
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    /** Devuelve cuántas unidades había al iniciar */
    public double getCantidadInicial() { return cantidadInicial; }

    /** Guarda cuántas unidades había al iniciar */
    public void setCantidadInicial(double cantidadInicial) { this.cantidadInicial = cantidadInicial; }

    /** Devuelve cuántas unidades quedaron al cerrar */
    public double getCantidadFinal() { return cantidadFinal; }

    /** Guarda cuántas unidades quedaron al cerrar */
    public void setCantidadFinal(double cantidadFinal) { this.cantidadFinal = cantidadFinal; }

    // =====================================================================
    // GETTERS Y SETTERS AUXILIARES (Datos extra traídos de otras tablas)
    // =====================================================================

    /** Devuelve el nombre del producto (ej: "Cerveza Águila") */
    public String getNombreProducto() { return nombreProducto; }

    /** Guarda el nombre del producto traído de la tabla PRODUCTO */
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    /** Devuelve el precio unitario del producto */
    public double getPrecioUnitario() { return precioUnitario; }

    /** Guarda el precio unitario del producto traído de la tabla PRODUCTO */
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
}
