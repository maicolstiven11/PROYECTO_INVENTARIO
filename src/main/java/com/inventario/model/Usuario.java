package com.inventario.model;

public class Usuario {
    // 1. ATRIBUTOS: Son exactamente las mismas columnas de tu tabla 'USUARIO' en MySQL
    private int idUsuario;
    private int idRol; // Es la clave foránea que conecta con la tabla ROL
    private String nombre;
    private String password;
    // NUEVOS CAMPOS
    private String email;
    private String telefono;

    // 2. CONSTRUCTOR VACÍO: Necesario para que algunas herramientas de Java funcionen
    public Usuario() {
    }

    // 3. CONSTRUCTOR CON TODO: Para crear un usuario "de una" con todos sus datos
    public Usuario(int idUsuario, int idRol, String nombre, String password, String email, String telefono) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
        this.nombre = nombre;
        this.password = password;
        this.email = email;
        this.telefono = telefono;
    }

    // 4. GETTERS y SETTERS:
    // Los 'Getters' sirven para LEER el dato (ej. usuario.getNombre())
    // Los 'Setters' sirven para ESCRIBIR/CAMBIAR el dato (ej. usuario.setNombre("Juan"))
    
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    // MÉTODO EXTRA PARA MOSTRAR EL ROL FÁCILMENTE EN EL JSP
    // Cuando pongas ${usuario.nombreRol}, Java llamará a este método
    public String getNombreRol() {
        if (this.idRol == 1) {
            return "Administrador";
        } else {
            return "Trabajador";
        }
    }
    
    // --- NUEVO: MANEJO DE PERMISOS ---
    private java.util.List<String> permisos = new java.util.ArrayList<>();

    public java.util.List<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(java.util.List<String> permisos) {
        this.permisos = permisos;
    }
    
    // Método helper para usar en JSP: ${usuarioLogueado.tienePermiso('CREAR_BAR')}
    public boolean tienePermiso(String nombrePermiso) {
        if (permisos == null) return false;
        for (String p : permisos) {
            if (p.equalsIgnoreCase(nombrePermiso)) {
                return true;
            }
        }
        return false;
    }
}
