package com.inventario.model;

import java.sql.Date; // Importación para manejar la fecha del gasto desde MySQL

/**
 * MODELO: Clase Gasto (Entidad/POJO)
 * 
 * Representa la tabla GASTO_DIARIO de la base de datos MySQL.
 * Un Gasto es un egreso diario registrado dentro de un inventario activo (ej: servicios, insumos).
 * 
 * FLUJO DE DATOS:
 * - CREACIÓN:    agregar_gasto.html → GastoServlet (doPost) → GastoDao.registrarGasto() → INSERT en tabla GASTO_DIARIO
 * - LECTURA:     GastoDao.listarGastos(idInventario) → GastoServlet?action=listar → visualizar_gastos.jsp (${gasto.descripcion})
 * - EN INFORMES: InformeDAO.obtenerResumenGastos() → InformeServlet → visualizar_informes.jsp
 * 
 * TABLAS RELACIONADAS:
 * - INVENTARIO: Cada gasto pertenece a un inventario (FK: id_inventario, viene de la sesión)
 */
public class Gasto {

    // =====================================================================
    // ATRIBUTOS PRIVADOS - Corresponden a columnas de la tabla GASTO_DIARIO
    // =====================================================================

    private int id_gastos;       // PK: id_gastos (INT, AUTO_INCREMENT). Identificador único del gasto.
    private int id_inventario;   // FK: id_inventario (INT). Viene de: session.getAttribute("idInventarioActual") en GastoServlet
    private int cantidad;        // cantidad (INT). Cantidad de unidades del gasto. Viene de: agregar_gasto.html → input name="cantidad"
    private Date fecha;          // fecha (DATE). Fecha del gasto. Viene de: agregar_gasto.html → input name="fecha" (type="date")
    private Double subtotal;     // subtotal (DECIMAL). Monto total del gasto. Viene de: agregar_gasto.html → input name="subtotal"
    private String descripcion;  // descripcion (TEXT). Detalle del gasto. Viene de: agregar_gasto.html → textarea name="descripcion"

    /**
     * CONSTRUCTOR VACÍO
     * Usado por: GastoDao.listarGastos() al crear objetos desde ResultSet.
     * Usado por: GastoServlet al crear un nuevo Gasto antes de enviarlo al DAO.
     */
    public Gasto(){
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** Retorna el ID del gasto. Usado en JSP como: ${gasto.id_gastos} en visualizar_gastos.jsp */
    public int getId_gastos(){
        return id_gastos;
    }

    /** Asigna el ID. Llamado desde: GastoDao con rs.getInt("id_gastos") */
    public void setId_gastos(int id_gastos){
        this.id_gastos = id_gastos;
    }

    /** Retorna el ID del inventario al que pertenece. Usado internamente por GastoDao para filtrar */
    public int getId_inventario(){
        return id_inventario;
    }

    /** Asigna el inventario. Viene de: GastoServlet → session.getAttribute("idInventarioActual") */
    public void setId_inventario(int id_inventario){
        this.id_inventario = id_inventario;
    }

    /** Retorna la cantidad. Usado en JSP como: ${gasto.cantidad} */
    public int getCantidad(){
        return cantidad;
    }

    /** Asigna cantidad. Viene de: GastoServlet → Integer.parseInt(request.getParameter("cantidad")) */
    public void setCantidad(int cantidad){
        this.cantidad = cantidad;
    }

    /** Retorna la fecha. Usado en JSP como: ${gasto.fecha} */
    public Date getFecha(){
        return fecha;
    }

    /** Asigna fecha. Viene de: GastoServlet → Date.valueOf(request.getParameter("fecha")) */
    public void setFecha(Date fecha){
        this.fecha = fecha;
    }

    /** Retorna el subtotal. Usado en JSP como: ${gasto.subtotal} con fmt:formatNumber */
    public Double getSubtotal(){
        return subtotal;
    }

    /** Asigna subtotal. Viene de: GastoServlet → Double.parseDouble(request.getParameter("subtotal")) */
    public void setSubtotal(Double subtotal){
        this.subtotal = subtotal;
    }

    /** Retorna la descripción. Usado en JSP como: ${gasto.descripcion} en visualizar_gastos.jsp */
    public String getDescripcion(){
        return descripcion;
    }

    /** Asigna descripción. Viene de: GastoServlet → request.getParameter("descripcion") */
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
}
