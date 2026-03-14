package com.inventario.model;

/**
 * MODELO: Clase Negocio (Entidad/POJO)
 * 
 * Representa la tabla NEGOCIO de la base de datos MySQL.
 * Un Negocio es un "bar" registrado por un Administrador.
 * 
 * FLUJO DE DATOS:
 * - CREACIÓN:  registroBar.html → NegocioServlet (doPost) → NegocioDAO.registrarNegocio() → INSERT en tabla NEGOCIO
 * - LECTURA:   NegocioDAO.listarNegocios() → NegocioServlet (doGet) → lista_bares.jsp (se muestra con ${bar.nombre})
 * - ELIMINACIÓN: lista_bares.jsp (botón borrar) → NegocioServlet?action=eliminar → NegocioDAO.eliminarNegocio()
 * 
 * TABLAS RELACIONADAS:
 * - INVENTARIO: Un negocio puede tener UN inventario activo a la vez (FK: id_negocio)
 * - USUARIO_NEGOCIO: Vincula trabajadores con negocios (FK: id_negocio, id_usuario)
 */
public class Negocio {

    // =====================================================================
    // ATRIBUTOS PRIVADOS - Corresponden a columnas de la tabla NEGOCIO
    // =====================================================================

    private int idNegocio;                   // PK: id_negocio (INT, AUTO_INCREMENT). Identificador único del bar.
    private String nombre;                   // nombre (VARCHAR 150). Nombre del bar. Viene de: registroBar.html → input name="nombre"
    private String direccion;                // direccion (VARCHAR 200). Dirección física. Viene de: registroBar.html → input name="direccion"
    private String estado;                   // estado (ENUM: 'activo', 'inactivo'). Se cambia en InventarioDAO al iniciar/cerrar inventario.
    private boolean tieneInventarioActivo;   // NO es columna de BD. Es un flag calculado en NegocioDAO.listarNegocios() para la vista lista_bares.jsp.

    /**
     * CONSTRUCTOR VACÍO
     * Usado por: NegocioDAO.listarNegocios() cuando crea objetos con new Negocio() y luego llena con setters.
     * Usado por: UsuarioDAO.obtenerNegocioAsignado() para retornar el negocio de un trabajador.
     */
    public Negocio() {
    }

    /**
     * CONSTRUCTOR CON PARÁMETROS
     * Permite crear un Negocio con todos los datos de una sola vez.
     * Usado por: NegocioDAO cuando lee los datos de un ResultSet de la BD.
     */
    public Negocio(int idNegocio, String nombre, String direccion, String estado) {
        this.idNegocio = idNegocio;   // Viene de: rs.getInt("id_negocio") en el DAO
        this.nombre = nombre;         // Viene de: rs.getString("nombre") en el DAO
        this.direccion = direccion;   // Viene de: rs.getString("direccion") en el DAO
        this.estado = estado;         // Viene de: rs.getString("estado") en el DAO
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** Retorna el ID del negocio. Usado en JSP como: ${bar.idNegocio} para armar enlaces */
    public int getIdNegocio() {
        return idNegocio;
    }

    /** Asigna el ID. Llamado desde: NegocioDAO después de leer de la BD con rs.getInt("id_negocio") */
    public void setIdNegocio(int idNegocio) {
        this.idNegocio = idNegocio;
    }

    /** Retorna el nombre del bar. Usado en JSP como: ${bar.nombre} en lista_bares.jsp */
    public String getNombre() {
        return nombre;
    }

    /** Asigna el nombre. Llamado desde: NegocioDAO con rs.getString("nombre") */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** Retorna la dirección. Usado en: perfil_admin.jsp o vistas de detalle */
    public String getDireccion() {
        return direccion;
    }

    /** Asigna la dirección. Llamado desde: NegocioDAO con rs.getString("direccion") */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /** Retorna el estado ('activo'/'inactivo'). Usado en JSP como: ${bar.estado} en lista_bares.jsp */
    public String getEstado() {
        return estado;
    }

    /** Asigna el estado. Llamado desde: NegocioDAO o InventarioDAO al cambiar estado del negocio */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Retorna si tiene inventario activo. NO viene de la BD directamente.
     * Se calcula en NegocioDAO.listarNegocios() con una subconsulta a la tabla INVENTARIO.
     * Usado en lista_bares.jsp: ${bar.tieneInventarioActivo} para mostrar "Ver Inventario" o "Iniciar Inventario"
     */
    public boolean isTieneInventarioActivo() {
        return tieneInventarioActivo;
    }

    /** Asigna el flag de inventario activo. Llamado desde: NegocioDAO.listarNegocios() */
    public void setTieneInventarioActivo(boolean tieneInventarioActivo) {
        this.tieneInventarioActivo = tieneInventarioActivo;
    }
}
