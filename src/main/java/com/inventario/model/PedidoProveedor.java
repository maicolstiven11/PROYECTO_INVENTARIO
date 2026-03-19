package com.inventario.model; // Paquete donde viven las clases molde

import java.sql.Date;       // Para manejar fechas compatibles con la base de datos
import java.util.List;       // Para poder tener una lista de detalles dentro del pedido

/**
 * Clase PedidoProveedor (Modelo / POJO).
 * 
 * Es el molde que representa UNA compra hecha a un proveedor.
 * Por ejemplo: "El 10 de marzo le pedimos a Bavaria cervezas por $500.000 + IVA".
 * Corresponde a la tabla 'pedidos_proveedor' de la base de datos.
 * Dentro puede tener varios renglones (DetallePedido) con los productos específicos.
 */
public class PedidoProveedor { // Declaración de la clase pública

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Columnas de la tabla)
    // =====================================================================

    private int idPedidoBase;     // ID único del pedido (Clave Primaria)
    private Date fechaPedido;     // Fecha en que se hizo el pedido (ej: 2026-03-10)
    private Date fechaEntrega;    // Fecha en que se espera recibir la mercancía (ej: 2026-03-15)
    private double totalPedido;   // Costo total con impuestos incluidos (subtotal + IVA)
    private double subtotal;      // Costo antes de impuestos
    private double ivaPedido;     // Valor del IVA cobrado
    private int idInventario;     // ID del inventario al que pertenece (Clave Foránea hacia tabla INVENTARIO)
    private int idProveedor;      // ID del proveedor que nos vende (Clave Foránea hacia tabla DATOS_PROVEEDOR)

    // =====================================================================
    // ATRIBUTOS AUXILIARES (No están en la tabla, los usamos para la pantalla)
    // =====================================================================

    private String nombreProveedor; // Nombre del proveedor (ej: "Bavaria"), viene de otra tabla por JOIN
    private List<DetallePedido> detalles; // Lista de renglones o ítems que compramos en este pedido

    /**
     * CONSTRUCTOR VACÍO.
     * Crea un PedidoProveedor sin datos, como una orden de compra en blanco.
     */
    public PedidoProveedor() {
    }

    /**
     * CONSTRUCTOR CON PARÁMETROS (Sobrecarga).
     * Crea un PedidoProveedor con todos los datos principales de una vez.
     * 
     * @param idPedidoBase ID del pedido
     * @param fechaPedido Fecha de la solicitud
     * @param fechaEntrega Fecha estimada de llegada
     * @param totalPedido Total con impuestos
     * @param subtotal Monto sin impuestos
     * @param ivaPedido Impuesto cobrado
     * @param idInventario ID del inventario asociado
     * @param idProveedor ID del proveedor
     */
    public PedidoProveedor(int idPedidoBase, Date fechaPedido, Date fechaEntrega, double totalPedido, double subtotal, double ivaPedido, int idInventario, int idProveedor) {
        this.idPedidoBase = idPedidoBase;   // Guarda el ID del pedido
        this.fechaPedido = fechaPedido;     // Guarda la fecha del pedido
        this.fechaEntrega = fechaEntrega;   // Guarda la fecha de entrega
        this.totalPedido = totalPedido;     // Guarda el total con IVA
        this.subtotal = subtotal;           // Guarda el subtotal sin IVA
        this.ivaPedido = ivaPedido;         // Guarda el monto del IVA
        this.idInventario = idInventario;   // Guarda a qué inventario pertenece
        this.idProveedor = idProveedor;     // Guarda qué proveedor nos vendió
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** Devuelve el ID del pedido */
    public int getIdPedidoBase() {
        return idPedidoBase;
    }

    /** Guarda el ID del pedido */
    public void setIdPedidoBase(int idPedidoBase) {
        this.idPedidoBase = idPedidoBase;
    }

    /** Devuelve la fecha en que se hizo el pedido */
    public Date getFechaPedido() {
        return fechaPedido;
    }

    /** Guarda la fecha del pedido */
    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    /** Devuelve la fecha estimada de entrega */
    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    /** Guarda la fecha de entrega */
    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    /** Devuelve el total con impuestos incluidos */
    public double getTotalPedido() {
        return totalPedido;
    }

    /** Guarda el total del pedido */
    public void setTotalPedido(double totalPedido) {
        this.totalPedido = totalPedido;
    }

    /** Devuelve el subtotal antes de impuestos */
    public double getSubtotal() {
        return subtotal;
    }

    /** Guarda el subtotal */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /** Devuelve el monto del IVA */
    public double getIvaPedido() {
        return ivaPedido;
    }

    /** Guarda el monto del IVA */
    public void setIvaPedido(double ivaPedido) {
        this.ivaPedido = ivaPedido;
    }

    /** Devuelve el ID del inventario al que pertenece */
    public int getIdInventario() {
        return idInventario;
    }

    /** Guarda el ID del inventario */
    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    /** Devuelve el ID del proveedor */
    public int getIdProveedor() {
        return idProveedor;
    }

    /** Guarda el ID del proveedor */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /** Devuelve el nombre del proveedor (ej: "Bavaria") */
    public String getNombreProveedor() {
        return nombreProveedor;
    }

    /** Guarda el nombre del proveedor (viene de otra tabla) */
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    /** Devuelve la lista de renglones (productos pedidos) dentro de este pedido */
    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    /** Guarda la lista de renglones que componen este pedido */
    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }
}
