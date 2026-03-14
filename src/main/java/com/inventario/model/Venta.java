package com.inventario.model;

import java.sql.Date; // Importación para manejar la fecha de la venta desde MySQL

/**
 * MODELO: Clase Venta (Entidad/POJO)
 * 
 * Representa la tabla VENTA de la base de datos MySQL.
 * Una Venta es una transacción de venta de productos en un inventario activo.
 * 
 * FLUJO DE DATOS:
 * - CREACIÓN:    agregar_venta.jsp (carrito) → VentaServlet?action=finalizar → VentaDAO.registrarVenta() → INSERT en tabla VENTA
 * - LECTURA:     VentaDAO.listarVentas(idInventario) → VentaServlet?action=listar → visualizar_ventas.jsp (${venta.totalVenta})
 * - EN INFORMES: InformeDAO.obtenerResumenVentas() → InformeServlet → visualizar_informes.jsp
 * 
 * TABLAS RELACIONADAS:
 * - INVENTARIO: Cada venta pertenece a un inventario (FK: id_inventario, viene de la sesión)
 * - DETALLE_VENTA: Los productos vendidos en esta venta (FK: id_venta)
 */
public class Venta {

    // =====================================================================
    // ATRIBUTOS PRIVADOS - Corresponden a columnas de la tabla VENTA
    // =====================================================================

    private int idVenta;        // PK: id_venta (INT, AUTO_INCREMENT). Se genera al registrar la venta.
    private int idInventario;   // FK: id_inventario (INT). Viene de: session.getAttribute("idInventarioActual") en VentaServlet
    private double totalVenta;  // total_venta (DECIMAL). Se calcula sumando los subtotales de cada DetalleVenta del carrito.
    private Date fechaVenta;    // fecha_venta (DATE). Se asigna automáticamente con la fecha actual en VentaDAO.

    /**
     * CONSTRUCTOR VACÍO
     * Usado por: VentaDAO.listarVentas() al crear objetos desde ResultSet.
     */
    public Venta() {
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** Retorna el ID de la venta. Usado en JSP como: ${venta.idVenta} en visualizar_ventas.jsp */
    public int getIdVenta() {
        return idVenta;
    }

    /** Asigna el ID. Llamado desde: VentaDAO con rs.getInt("id_venta") */
    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    /** Retorna el ID del inventario al que pertenece. Usado internamente por VentaDAO para filtrar */
    public int getIdInventario() {
        return idInventario;
    }

    /** Asigna el inventario. Viene de: session.getAttribute("idInventarioActual") en VentaServlet */
    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    /** Retorna el total de la venta. Usado en JSP como: ${venta.totalVenta} con fmt:formatNumber */
    public double getTotalVenta() {
        return totalVenta;
    }

    /** Asigna el total. Se calcula en VentaServlet sumando subtotales del carrito en sesión */
    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }

    /** Retorna fecha. Usado en JSP como: ${venta.fechaVenta} en visualizar_ventas.jsp */
    public Date getFechaVenta() {
        return fechaVenta;
    }

    /** Asigna la fecha. Viene de: VentaDAO con new Date(System.currentTimeMillis()) o rs.getDate("fecha_venta") */
    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
}
