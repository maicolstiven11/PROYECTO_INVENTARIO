package com.inventario.model;

/**
 * MODELO: Clase Usuario (Entidad/POJO)
 * 
 * Implementa: RF-01 (Registrar Usuario), RF-02 (Iniciar Sesión), RF-03 (Gestionar Roles y Permisos)
 * Cumple: RNF-13 (Arquitectura MVC - Capa Modelo separada de Controlador y Vista)
 * 
 * Esta clase es la representación en Java de la tabla USUARIO de la base de datos MySQL.
 * Funciona como una "caja de transporte" para mover datos del usuario entre capas (Vista → Controlador → DAO → Base de Datos).
 * Cada atributo privado corresponde a una columna de la tabla USUARIO.
 */
public class Usuario {

    // =====================================================================
    // ATRIBUTOS PRIVADOS (Encapsulamiento - Principio de POO)
    // RF-01: Estos atributos almacenan los datos del usuario para registro
    // RF-02: Estos mismos atributos se llenan al validar el login
    // =====================================================================

    private int idUsuario;    // RF-01, RF-02: Clave primaria (PK) de la tabla USUARIO. Se genera automáticamente en la BD (AUTO_INCREMENT).
    private int idRol;        // RF-01, RF-03: Clave foránea (FK) que conecta con la tabla ROL. Determina si es Administrador (1) o Trabajador (2).
    private String nombre;    // RF-01: Campo obligatorio. Nombre del usuario ingresado en el formulario de registro.
    private String password;  // RF-01, RF-02: Campo obligatorio. Contraseña del usuario. RNF-01: PENDIENTE - Debería almacenarse cifrada con BCrypt.
    private String email;     // RF-01, RF-02, RF-31: Correo electrónico. Se almacena en tabla separada CORREO_USUARIO. RF-32: Debe ser único.
    private String telefono;  // RF-01: Teléfono opcional. Se almacena en tabla separada TELEFONO_USUARIO.

    /**
     * CONSTRUCTOR VACÍO (sin parámetros)
     * Necesario para que Java pueda crear un objeto Usuario vacío con "new Usuario()"
     * y luego llenarlo con los setters.
     * Se usa en: LoginServlet (línea dao.validarLogin), RegistroServlet (new Usuario())
     */
    public Usuario() {
    }

    /**
     * CONSTRUCTOR CON PARÁMETROS
     * Permite crear un objeto Usuario con todos sus datos de una sola vez.
     * Ejemplo: new Usuario(1, 1, "Juan", "1234", "juan@mail.com", "3001234567")
     */
    public Usuario(int idUsuario, int idRol, String nombre, String password, String email, String telefono) {
        this.idUsuario = idUsuario;  // "this" se refiere al atributo de ESTA clase, no al parámetro
        this.idRol = idRol;
        this.nombre = nombre;
        this.password = password;
        this.email = email;
        this.telefono = telefono;
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // Los Getters permiten LEER un atributo privado desde fuera de la clase.
    // Los Setters permiten ESCRIBIR/MODIFICAR un atributo privado desde fuera.
    // Sin estos métodos, nadie podría acceder a los datos porque son "private".
    // =====================================================================

    /** RF-01, RF-02: Devuelve el ID único del usuario en la BD */
    public int getIdUsuario() {
        return idUsuario;
    }

    /** RF-01: Asigna el ID del usuario (lo usa el DAO después de insertar en BD) */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /** RF-03: Devuelve el ID del rol (1=Admin, 2=Trabajador). Usado en JSP para control de acceso: ${usuarioLogueado.idRol == 1} */
    public int getIdRol() {
        return idRol;
    }

    /** RF-01, RF-03: Asigna el rol al usuario */
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    /** RF-01: Devuelve el nombre del usuario */
    public String getNombre() {
        return nombre;
    }

    /** RF-01: Asigna el nombre del usuario */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** RF-02: Devuelve la contraseña (para comparar en login). RNF-01: PENDIENTE cifrado */
    public String getPassword() {
        return password;
    }

    /** RF-01: Asigna la contraseña del usuario */
    public void setPassword(String password) {
        this.password = password;
    }

    /** RF-01, RF-02: Devuelve el correo electrónico */
    public String getEmail() {
        return email;
    }

    /** RF-01: Asigna el correo electrónico */
    public void setEmail(String email) {
        this.email = email;
    }

    /** RF-01: Devuelve el teléfono */
    public String getTelefono() {
        return telefono;
    }

    /** RF-01: Asigna el teléfono */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    /**
     * RF-03: Método auxiliar para mostrar el nombre del rol en las vistas JSP.
     * En el JSP se usa así: ${usuarioLogueado.nombreRol}
     * Java automáticamente llama a getNombreRol() cuando encuentra .nombreRol en el JSP.
     * RNF-04: Usado para mostrar el rol en la interfaz de control de acceso.
     */
    public String getNombreRol() {
        if (this.idRol == 1) {
            return "Administrador";  // RF-03: Rol Administrador (id_rol = 1)
        } else {
            return "Trabajador";     // RF-03: Rol Trabajador (id_rol = 2, valor por defecto según RF-01)
        }
    }
    
    // =====================================================================
    // RF-03: SISTEMA DE PERMISOS DINÁMICOS
    // Los permisos se cargan desde la BD (tablas PERMISO y ROL_PERMISOS)
    // al momento del login y se almacenan en esta lista.
    // RNF-04: Implementa control de acceso basado en permisos.
    // =====================================================================
    
    /** RF-03: Lista de nombres de permisos asignados al rol del usuario */
    private java.util.List<String> permisos = new java.util.ArrayList<>();

    /** RF-03: Devuelve la lista completa de permisos del usuario */
    public java.util.List<String> getPermisos() {
        return permisos;
    }

    /** RF-03: Asigna la lista de permisos (llamado desde UsuarioDAO.validarLogin) */
    public void setPermisos(java.util.List<String> permisos) {
        this.permisos = permisos;
    }
    
    /**
     * RF-03, RNF-04: Verifica si el usuario tiene un permiso específico.
     * Se usa en JSP así: ${usuarioLogueado.tienePermiso('CREAR_BAR')}
     * Recorre la lista de permisos buscando coincidencia (sin importar mayúsculas/minúsculas).
     * 
     * @param nombrePermiso El nombre del permiso a verificar (ejemplo: "EDITAR_PRODUCTOS")
     * @return true si el usuario tiene el permiso, false si no lo tiene
     */
    public boolean tienePermiso(String nombrePermiso) {
        if (permisos == null) return false;          // Si no hay permisos cargados, denegar
        for (String p : permisos) {                  // Recorrer toda la lista de permisos
            if (p.equalsIgnoreCase(nombrePermiso)) { // Comparar sin importar MAYÚSCULAS/minúsculas
                return true;                         // ¡Lo tiene! Permitir acceso
            }
        }
        return false;                                // No lo encontró. Denegar acceso
    }
}
