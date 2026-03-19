package com.inventario.model; // Paquete donde viven las clases molde

/**
 * Clase DetallePedido (Modelo / POJO).
 * 
 * Es el molde que representa UN renglón de UN pedido al proveedor.
 * Por ejemplo: "Le pedimos 50 cervezas Águila a Bavaria".
 * Corresponde a la tabla 'detalle_pedidos' de la base de datos.
 */
public class DetallePedido { // Declaración de la clase pública

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Cada uno es una columna de la tabla)
    // =====================================================================

    private int idPedidoRegistro;     // ID único de este renglón del pedido (Clave Primaria)
    private int idPedidoBase;         // ID del pedido general al que pertenece (Clave Foránea hacia tabla PEDIDOS_PROVEEDOR)
    private int idInvDetalle;         // ID que lo relaciona con el producto en el inventario (Clave Foránea hacia INVENTARIO_DETALLE)
    private int cantidadPedida;       // Cuántas unidades pedimos (ej: 50 cervezas)
    private double precioUnitarioReal;// Precio de cada unidad al momento de comprar (ej: $2.500 c/u)

    // =====================================================================
    // ATRIBUTOS AUXILIARES (Se usan solo en pantalla, no vienen directo de esta tabla)
    // =====================================================================

    private String nombreProducto;    // Nombre del producto pedido (ej: "Cerveza Águila"), viene de otra tabla
    private double subtotalCalculado; // Resultado de multiplicar cantidad x precio, calculado en el programa

    /**
     * CONSTRUCTOR VACÍO.
     * Crea un DetallePedido sin datos, como un formulario en blanco.
     */
    public DetallePedido() {
    }

    /**
     * CONSTRUCTOR CON PARÁMETROS (Sobrecarga).
     * Crea un DetallePedido con todos los datos principales de una vez.
     * 
     * @param idPedidoRegistro ID de este renglón
     * @param idPedidoBase ID del pedido general
     * @param idInvDetalle ID del enlace con el inventario
     * @param cantidadPedida Cuántas unidades se pidieron
     * @param precioUnitarioReal Precio de cada unidad
     */
    public DetallePedido(int idPedidoRegistro, int idPedidoBase, int idInvDetalle, int cantidadPedida, double precioUnitarioReal) {
        this.idPedidoRegistro = idPedidoRegistro;       // "this" diferencia el atributo de la clase del parámetro
        this.idPedidoBase = idPedidoBase;               // Guarda a qué pedido general pertenece
        this.idInvDetalle = idInvDetalle;                // Guarda la relación con el inventario
        this.cantidadPedida = cantidadPedida;            // Guarda cuántas unidades pidieron
        this.precioUnitarioReal = precioUnitarioReal;    // Guarda el precio por unidad
    }

    // =====================================================================
    // GETTERS Y SETTERS (Para leer y escribir los atributos de forma segura)
    // =====================================================================

    /** Devuelve el ID único de este renglón del pedido */
    public int getIdPedidoRegistro() {
        return idPedidoRegistro;
    }

    /** Guarda el ID único de este renglón del pedido */
    public void setIdPedidoRegistro(int idPedidoRegistro) {
        this.idPedidoRegistro = idPedidoRegistro;
    }

    /** Devuelve el ID del pedido general al que pertenece */
    public int getIdPedidoBase() {
        return idPedidoBase;
    }

    /** Guarda el ID del pedido general */
    public void setIdPedidoBase(int idPedidoBase) {
        this.idPedidoBase = idPedidoBase;
    }

    /** Devuelve el ID de la relación con el inventario */
    public int getIdInvDetalle() {
        return idInvDetalle;
    }

    /** Guarda el ID de la relación con el inventario */
    public void setIdInvDetalle(int idInvDetalle) {
        this.idInvDetalle = idInvDetalle;
    }

    /** Devuelve cuántas unidades se pidieron */
    public int getCantidadPedida() {
        return cantidadPedida;
    }

    /** Guarda cuántas unidades se pidieron */
    public void setCantidadPedida(int cantidadPedida) {
        this.cantidadPedida = cantidadPedida;
    }

    /** Devuelve el precio de cada unidad */
    public double getPrecioUnitarioReal() {
        return precioUnitarioReal;
    }

    /** Guarda el precio de cada unidad */
    public void setPrecioUnitarioReal(double precioUnitarioReal) {
        this.precioUnitarioReal = precioUnitarioReal;
    }

    /** Devuelve el nombre del producto pedido (ej: "Cerveza Águila") */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /** Guarda el nombre del producto (viene de otra tabla) */
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    /** Devuelve el subtotal calculado (cantidad x precio) */
    public double getSubtotalCalculado() {
        return subtotalCalculado;
    }

    /** Guarda el subtotal calculado */
    public void setSubtotalCalculado(double subtotalCalculado) {
        this.subtotalCalculado = subtotalCalculado;
    }
}
