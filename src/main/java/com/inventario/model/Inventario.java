package com.inventario.model;

import java.sql.Date; // Importación para manejar fechas de la BD MySQL (fecha_inicio del inventario)

/**
 * MODELO: Clase Inventario (Entidad/POJO)
 * 
 * Representa la tabla INVENTARIO de la base de datos MySQL.
 * Un Inventario es un periodo de control contable de un bar (negocio).
 * Solo puede haber UN inventario activo por negocio a la vez.
 * 
 * FLUJO DE DATOS:
 * - CREACIÓN:    Inicio_inv.html → InventarioServlet?action=iniciar → InventarioDAO.iniciarInventario() → INSERT en tabla INVENTARIO
 * - LECTURA:     InventarioDAO.obtenerInventarioActivo(idNegocio) → InventarioServlet?action=entrar → menu_inventario.jsp
 * - CIERRE:      inventario_cierre.jsp → InventarioServlet?action=cerrar → InventarioDAO.cerrarInventario() → UPDATE estado='inactivo'
 * - EN LOGIN:    LoginServlet → InventarioDAO.obtenerInventarioActivo() → Carga idInventarioActual en sesión para trabajadores
 * 
 * TABLAS RELACIONADAS:
 * - NEGOCIO: Cada inventario pertenece a un negocio (FK: id_negocio)
 * - INVENTARIO_DETALLE: Los productos con su stock inicial (FK: id_inventario)
 * - VENTA: Las ventas se registran contra este inventario (FK: id_inventario vía sesión)
 * - GASTO_DIARIO: Los gastos se vinculan al inventario activo (FK: id_inventario)
 * - PEDIDOS_PROVEEDOR: Los pedidos se vinculan al inventario activo
 */
public class Inventario {

    // =====================================================================
    // ATRIBUTOS PRIVADOS - Corresponden a columnas de la tabla INVENTARIO
    // =====================================================================

    private int idInventario;   // PK: id_inventario (INT, AUTO_INCREMENT). Se genera al crear nuevo inventario.
    private int idNegocio;      // FK: id_negocio (INT). Referencia a la tabla NEGOCIO. Viene de: Inicio_inv.html → input hidden name="idNegocio"
    private Date fechaInicio;   // fecha_inicio (DATE). Fecha de apertura del inventario. Viene de: Inicio_inv.html → input name="fecha" (type="date")
    private String tipoControl; // tipo_control (VARCHAR). Tipo de control: 'semanal' o 'mensual'. Viene de: Inicio_inv.html → select name="tipo"
    private String estado;      // estado (ENUM: 'activo','inactivo'). Se crea como 'activo', cambia a 'inactivo' al cerrar.

    /**
     * CONSTRUCTOR VACÍO
     * Usado por: InventarioDAO.obtenerInventarioActivo() cuando crea el objeto y lo llena con setters.
     * Usado por: LoginServlet al buscar el inventario activo del trabajador.
     */
    public Inventario() {
    }

    /**
     * CONSTRUCTOR COMPLETO
     * Permite crear un Inventario con todos los datos de una sola vez.
     * Usado por: InventarioDAO al leer datos de un ResultSet.
     */
    public Inventario(int idInventario, int idNegocio, Date fechaInicio, String tipoControl, String estado) {
        this.idInventario = idInventario; // Viene de: rs.getInt("id_inventario")
        this.idNegocio = idNegocio;       // Viene de: rs.getInt("id_negocio")
        this.fechaInicio = fechaInicio;   // Viene de: rs.getDate("fecha_inicio")
        this.tipoControl = tipoControl;   // Viene de: rs.getString("tipo_control")
        this.estado = estado;             // Viene de: rs.getString("estado")
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** Retorna el ID del inventario. Se guarda en sesión como: session.setAttribute("idInventarioActual", inv.getIdInventario()) */
    public int getIdInventario() {
        return idInventario;
    }

    /** Asigna el ID. Llamado desde: InventarioDAO con rs.getInt("id_inventario") */
    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    /** Retorna el ID del negocio al que pertenece. Se guarda en sesión como: session.setAttribute("idNegocioActual", idNegocio) */
    public int getIdNegocio() {
        return idNegocio;
    }

    /** Asigna el ID del negocio. Viene de: InventarioServlet → Integer.parseInt(request.getParameter("idNegocio")) */
    public void setIdNegocio(int idNegocio) {
        this.idNegocio = idNegocio;
    }

    /** Retorna la fecha de inicio. Usada en: reporte_descuadre.jsp y lista_informes.jsp */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /** Asigna la fecha. Viene de: InventarioServlet → Date.valueOf(request.getParameter("fecha")) */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /** Retorna el tipo de control ('semanal'/'mensual'). Usado en vistas de información */
    public String getTipoControl() {
        return tipoControl;
    }

    /** Asigna el tipo. Viene de: InventarioServlet → request.getParameter("tipo") */
    public void setTipoControl(String tipoControl) {
        this.tipoControl = tipoControl;
    }

    /** Retorna el estado ('activo'/'inactivo'). Usado para determinar si se puede operar en el inventario */
    public String getEstado() {
        return estado;
    }

    /** Asigna el estado. Modificado en: InventarioDAO.cerrarInventario() cuando cambia a 'inactivo' */
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
