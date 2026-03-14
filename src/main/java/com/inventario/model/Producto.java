package com.inventario.model;

import java.sql.Date; // Importación de java.sql.Date para manejar fechas de la BD MySQL

/**
 * MODELO: Clase Producto (Entidad/POJO)
 * 
 * Representa la tabla PRODUCTO de la base de datos MySQL.
 * Un Producto es un artículo de venta registrado por el Administrador.
 * 
 * FLUJO DE DATOS:
 * - CREACIÓN:    Registro_produc.html → ProductoServlet (doPost) → ProductoDAO.registrarProducto() → INSERT en tabla PRODUCTO
 * - LECTURA:     ProductoDAO.listarProductos() → ProductoServlet (doGet) → editar_productos.jsp (se muestra con ${prod.nombre})
 * - EDICIÓN:     formulario_editar_producto.jsp → ProductoServlet?action=actualizar → ProductoDAO.actualizarProducto()
 * - EN VENTAS:   VentaServlet → ProductoDAO.listarProductos() → agregar_venta.jsp (en el select de productos)
 * - EN PEDIDOS:  DetalleInventarioDAO → agregar_pedido.jsp (en el select de productos del inventario)
 * 
 * TABLAS RELACIONADAS:
 * - INVENTARIO_DETALLE: Vincula producto con inventario (FK: id_producto). Controla stock.
 * - DETALLE_VENTA: Cada venta registra qué producto se vendió (FK: id_inv_detalle → INVENTARIO_DETALLE)
 */
public class Producto {

    // =====================================================================
    // ATRIBUTOS PRIVADOS - Corresponden a columnas de la tabla PRODUCTO
    // =====================================================================

    private int idProducto;        // PK: id_producto (INT, AUTO_INCREMENT). Identificador único del producto.
    private String nombre;         // nombre (VARCHAR 100). Nombre del producto. Viene de: Registro_produc.html → input name="nombre"
    private String marca;          // marca (VARCHAR 100). Marca del producto. Viene de: Registro_produc.html → input name="marca"
    private double precioUnitario; // precio_unitario (DECIMAL). Precio de venta. Viene de: Registro_produc.html → input name="precio"
    private String tipo;           // tipo (ENUM: 'bebida','snack','dulce','cigarro'). Viene de: Registro_produc.html → select name="tipo"
    private String imagen;         // imagen (VARCHAR). Ruta o nombre del archivo de imagen. Viene de: input name="imagen" (type="file")
    private Date fechaVencimiento; // fecha_vencimiento (DATE). Viene de: Registro_produc.html → input name="fecha_vencimiento" (type="date")
    private String cantidadMedida; // cantidad_medida (VARCHAR). Ej: "750ml", "500gr". Viene de: input name="cantidad_medida"

    /**
     * CONSTRUCTOR VACÍO
     * Usado por: ProductoDAO.listarProductos() cuando crea objetos con new Producto() y llena con setters desde ResultSet.
     */
    public Producto() {
    }

    /**
     * CONSTRUCTOR COMPLETO
     * Permite crear un Producto con todos los datos de una sola vez.
     * Usado por: ProductoDAO al leer todos los campos de un ResultSet.
     */
    public Producto(int idProducto, String nombre, String marca, double precioUnitario, String tipo, String imagen, Date fechaVencimiento, String cantidadMedida) {
        this.idProducto = idProducto;           // Viene de: rs.getInt("id_producto")
        this.nombre = nombre;                   // Viene de: rs.getString("nombre")
        this.marca = marca;                     // Viene de: rs.getString("marca")
        this.precioUnitario = precioUnitario;   // Viene de: rs.getDouble("precio_unitario")
        this.tipo = tipo;                       // Viene de: rs.getString("tipo")
        this.imagen = imagen;                   // Viene de: rs.getString("imagen")
        this.fechaVencimiento = fechaVencimiento; // Viene de: rs.getDate("fecha_vencimiento")
        this.cantidadMedida = cantidadMedida;   // Viene de: rs.getString("cantidad_medida")
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** Retorna el ID del producto. Usado en JSP como: ${prod.idProducto} o ${p.idProducto} en selects */
    public int getIdProducto() {
        return idProducto;
    }

    /** Asigna el ID. Llamado desde: ProductoDAO con rs.getInt("id_producto") */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    /** Retorna el nombre. Usado en JSP como: ${prod.nombre} en editar_productos.jsp y ${p.nombre} en agregar_venta.jsp */
    public String getNombre() {
        return nombre;
    }

    /** Asigna el nombre. Viene de: ProductoServlet → request.getParameter("nombre") */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** Retorna la marca. Usado en JSP como: ${prod.marca} y ${p.marca} */
    public String getMarca() {
        return marca;
    }

    /** Asigna la marca. Viene de: ProductoServlet → request.getParameter("marca") */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /** Retorna el precio unitario. Usado en JSP como: ${prod.precioUnitario} y ${p.precioUnitario} */
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    /** Asigna el precio. Viene de: ProductoServlet → Double.parseDouble(request.getParameter("precio")) */
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /** Retorna el tipo de producto. Usado en JSP como: ${prod.tipo} (para marcar selected en el select de editar) */
    public String getTipo() {
        return tipo;
    }

    /** Asigna el tipo. Viene de: ProductoServlet → request.getParameter("tipo") */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /** Retorna la ruta de la imagen. Usado en JSP como: ${prod.imagen} */
    public String getImagen() {
        return imagen;
    }

    /** Asigna la imagen. Viene de: ProductoServlet → request.getParameter("imagen") */
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    /** Retorna la fecha de vencimiento. Usado en JSP con fmt:formatDate para formato legible */
    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    /** Asigna la fecha. Viene de: ProductoServlet → Date.valueOf(request.getParameter("fecha_vencimiento")) */
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /** Retorna la cantidad/medida. Usado en JSP como: ${prod.cantidadMedida} */
    public String getCantidadMedida() {
        return cantidadMedida;
    }

    /** Asigna la cantidad/medida. Viene de: ProductoServlet → request.getParameter("cantidad_medida") */
    public void setCantidadMedida(String cantidadMedida) {
        this.cantidadMedida = cantidadMedida;
    }
}
