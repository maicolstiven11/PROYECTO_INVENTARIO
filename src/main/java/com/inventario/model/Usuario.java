package com.inventario.model; // Paquete donde viven las clases molde

/**
 * Clase Usuario (Modelo / POJO).
 * 
 * Es el molde que representa UNA persona registrada en el sistema.
 * Puede ser un Administrador (dueño de bares) o un Trabajador (cajero).
 * Corresponde a la tabla 'usuario' de la base de datos.
 */
public class Usuario { // Declaración de la clase pública

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Columnas de la tabla)
    // =====================================================================

    private int idUsuario;    // ID único del usuario (Clave Primaria, la genera la BD)
    private int idRol;        // ID del rol: 1 = Administrador, 2 = Trabajador (Clave Foránea hacia tabla ROL)
    private String nombre;    // Nombre completo de la persona (ej: "Juan Pérez")
    private String password;  // Contraseña del usuario (se guarda cifrada con SHA-256)
    private String email;     // Correo electrónico principal
    private String telefono;  // Teléfono de contacto

    /**
     * CONSTRUCTOR VACÍO.
     * Crea un Usuario sin datos, como un formulario de registro en blanco.
     * Los datos se llenan después con los setters.
     */
    public Usuario() {
    }

    /**
     * CONSTRUCTOR CON PARÁMETROS (Sobrecarga).
     * Crea un Usuario con todos los datos de una sola vez.
     * 
     * @param idUsuario ID del usuario
     * @param idRol ID del rol (1=Admin, 2=Trabajador)
     * @param nombre Nombre completo
     * @param password Contraseña
     * @param email Correo electrónico
     * @param telefono Teléfono de contacto
     */
    public Usuario(int idUsuario, int idRol, String nombre, String password, String email, String telefono) {
        this.idUsuario = idUsuario;  // "this" diferencia el atributo de la clase del parámetro del constructor
        this.idRol = idRol;          // Guarda el rol
        this.nombre = nombre;        // Guarda el nombre
        this.password = password;    // Guarda la contraseña
        this.email = email;          // Guarda el correo
        this.telefono = telefono;    // Guarda el teléfono
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    /** Devuelve el ID del usuario */
    public int getIdUsuario() {
        return idUsuario;
    }

    /** Guarda el ID del usuario */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /** Devuelve el ID del rol (1=Admin, 2=Trabajador) */
    public int getIdRol() {
        return idRol;
    }

    /** Guarda el ID del rol */
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    /** Devuelve el nombre completo del usuario */
    public String getNombre() {
        return nombre;
    }

    /** Guarda el nombre del usuario */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** Devuelve la contraseña del usuario */
    public String getPassword() {
        return password;
    }

    /** Guarda la contraseña del usuario */
    public void setPassword(String password) {
        this.password = password;
    }

    /** Devuelve el correo electrónico del usuario */
    public String getEmail() {
        return email;
    }

    /** Guarda el correo electrónico */
    public void setEmail(String email) {
        this.email = email;
    }

    /** Devuelve el teléfono del usuario */
    public String getTelefono() {
        return telefono;
    }

    /** Guarda el teléfono del usuario */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    /**
     * Método auxiliar que traduce el número del rol a un texto legible.
     * Si el idRol es 1, devuelve "Administrador". Si es otro, devuelve "Trabajador".
     * Se usa en las páginas JSP para mostrar el rol con nombre en vez de número.
     * 
     * @return "Administrador" o "Trabajador" según el rol del usuario
     */
    public String getNombreRol() {
        if (this.idRol == 1) {       // Si el rol es 1
            return "Administrador";  // Es el dueño
        } else {                     // Si es cualquier otro número
            return "Trabajador";     // Es un cajero
        }
    }
    
    // =====================================================================
    // LISTA DE PERMISOS (Para controlar qué puede hacer cada usuario)
    // =====================================================================
    
    /** 
     * Lista (colección) de permisos del usuario.
     * Se crea vacía al principio y se llena desde la base de datos.
     * Ejemplo: ["VENTAS", "GASTOS", "INFORMES"]
     */
    private java.util.List<String> permisos = new java.util.ArrayList<>(); 

    /** Devuelve la lista de permisos del usuario */
    public java.util.List<String> getPermisos() {
        return permisos;
    }

    /** Guarda una lista completa de permisos en el usuario */
    public void setPermisos(java.util.List<String> permisos) {
        this.permisos = permisos;
    }
    
    /**
     * Método que verifica si el usuario tiene un permiso específico.
     * Recorre la lista de permisos buscando el nombre que le pasemos.
     * 
     * @param nombrePermiso El permiso a buscar (ej: "VENTAS")
     * @return true si lo tiene, false si no
     */
    public boolean tienePermiso(String nombrePermiso) {
        if (permisos == null) return false;          // Si la lista no existe, no tiene permisos
        for (String p : permisos) {                  // Recorremos permiso por permiso
            if (p.equalsIgnoreCase(nombrePermiso)) { // Comparamos sin importar mayúsculas/minúsculas
                return true;                         // ¡Lo encontró! Sí tiene ese permiso
            }
        }
        return false;                                // Terminó de buscar y no lo encontró
    }
}
