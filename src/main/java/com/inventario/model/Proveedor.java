package com.inventario.model;

/**
 * MODELO: Clase Proveedor (Entidad/POJO)
 * 
 * Representa la tabla PROVEEDOR de la base de datos MySQL.
 * Un Proveedor es una empresa o persona que suministra productos al bar.
 * 
 * FLUJO DE DATOS:
 * - CREACIÓN:    Registro_datos_prv.html → ProveedorServlet (doPost) → ProveedorDAO.registrarProveedor() → INSERT en tabla PROVEEDOR
 * - LECTURA:     ProveedorDAO.listarProveedores() → ProveedorServlet (doGet) → lista_proveedores.jsp (${prov.nombreProveedor})
 * - EN PEDIDOS:  PedidoServlet?action=nuevo → ProveedorDAO.listarProveedores() → agregar_pedido.jsp (select de proveedores)
 * 
 * TABLAS RELACIONADAS:
 * - PEDIDOS_PROVEEDOR: Los pedidos se vinculan a un proveedor (FK: id_proveedor)
 */
public class Proveedor {

    // =====================================================================
    // ATRIBUTOS PRIVADOS - Corresponden a columnas de la tabla PROVEEDOR
    // =====================================================================

    private int idProveedor;         // PK: id_proveedor (INT, AUTO_INCREMENT). Identificador único.
    private String nombreProveedor;  // nombre_proveedor (VARCHAR). Nombre de la empresa. Viene de: Registro_datos_prv.html → input name="nombre_proveedor"
    private String contacto;         // contacto (VARCHAR). Nombre de la persona de contacto. Viene de: input name="contacto"
    private String telefono;         // telefono (VARCHAR). Teléfono del proveedor. Viene de: input name="telefono"
    private String correo;           // correo (VARCHAR). Correo del proveedor. Viene de: input name="correo"

    /**
     * CONSTRUCTOR VACÍO
     * Usado por: ProveedorDAO.listarProveedores() al crear objetos desde ResultSet.
     */
    public Proveedor() {
    }

    /**
     * CONSTRUCTOR COMPLETO
     * Permite crear un Proveedor con todos los datos.
     * Usado por: ProveedorDAO al leer datos de un ResultSet.
     */
    public Proveedor(int idProveedor, String nombreProveedor, String contacto, String telefono, String correo) {
        this.idProveedor = idProveedor;           // Viene de: rs.getInt("id_proveedor")
        this.nombreProveedor = nombreProveedor;   // Viene de: rs.getString("nombre_proveedor")
        this.contacto = contacto;                 // Viene de: rs.getString("contacto")
        this.telefono = telefono;                 // Viene de: rs.getString("telefono")
        this.correo = correo;                     // Viene de: rs.getString("correo")
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** Retorna el ID. Usado en JSP como: ${prov.idProveedor} para armar enlaces */
    public int getIdProveedor() {
        return idProveedor;
    }

    /** Asigna el ID. Llamado desde: ProveedorDAO con rs.getInt("id_proveedor") */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /** Retorna el nombre. Usado en JSP como: ${prov.nombreProveedor} en lista_proveedores.jsp y en el select de agregar_pedido.jsp */
    public String getNombreProveedor() {
        return nombreProveedor;
    }

    /** Asigna el nombre. Viene de: ProveedorServlet → request.getParameter("nombre_proveedor") */
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    /** Retorna la persona de contacto. Usado en JSP como: ${prov.contacto} */
    public String getContacto() {
        return contacto;
    }

    /** Asigna contacto. Viene de: ProveedorServlet → request.getParameter("contacto") */
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    /** Retorna teléfono. Usado en JSP como: ${prov.telefono} */
    public String getTelefono() {
        return telefono;
    }

    /** Asigna teléfono. Viene de: ProveedorServlet → request.getParameter("telefono") */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /** Retorna correo. Usado en JSP como: ${prov.correo} */
    public String getCorreo() {
        return correo;
    }

    /** Asigna correo. Viene de: ProveedorServlet → request.getParameter("correo") */
    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
